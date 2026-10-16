import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface Bloque {
  id?: number;
  codigo: string;
  curso?: string;
  dia: string;
  hora: number;
  version: number;
}

// mismos nombres que devuelve GET /bloques/parametros
interface Parametros {
  horaInicio: number;
  horaFin: number;
  sabadoFin: number;
  almuerzoInicio: number;
  almuerzoFin: number;
  duracionSesion: number;
  maxSesionesSemana: number;
}

type TipoAlerta = 'exito' | 'info' | 'aviso' | 'error';

interface Alerta {
  id: number;
  tipo: TipoAlerta;
  texto: string;
  hora: string;
}

@Component({
  selector: 'app-horario',
  standalone: false,
  templateUrl: './horario.html',
  styleUrls: ['./horario.scss']
})
export class Horario implements OnInit, OnDestroy {

  private readonly apiUrl = 'http://localhost:8080/bloques';
  private readonly wsUrl = 'ws://localhost:8080/ws/horario';

  dias: string[] = ['LUN', 'MAR', 'MIE', 'JUE', 'VIE', 'SAB'];

  // valores del caso de estudio, se reemplazan con los del servidor al cargar
  parametros: Parametros = {
    horaInicio: 7,
    horaFin: 22,
    sabadoFin: 13,
    almuerzoInicio: 12,
    almuerzoFin: 13,
    duracionSesion: 2,
    maxSesionesSemana: 4
  };
  horas: number[] = [];

  bloques: Bloque[] = [];
  conectado: boolean = false;

  alertas: Alerta[] = [];   // las que se ven en pantalla
  historial: Alerta[] = []; // registro de las últimas acciones

  private ws?: WebSocket;
  private bloqueArrastrado: Bloque | null = null;
  private pendientes = new Map<string, { dia: string; hora: number }>();
  private contadorAlertas = 0;
  private destruido = false;

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) {
    this.armarHoras();
  }

  ngOnInit(): void {
    this.cargarParametros();
    this.cargarBloques();
    this.conectar();
  }

  ngOnDestroy(): void {
    this.destruido = true;
    this.ws?.close();
  }

  // ---------- alertas ----------

  private refrescar(): void {
    if (!this.destruido) {
      this.cd.detectChanges();
    }
  }

  private alerta(tipo: TipoAlerta, texto: string): void {
    const a: Alerta = {
      id: ++this.contadorAlertas,
      tipo,
      texto,
      hora: new Date().toLocaleTimeString()
    };
    this.alertas.push(a);
    this.historial.unshift(a);
    if (this.historial.length > 15) {
      this.historial.pop();
    }
    setTimeout(() => this.cerrarAlerta(a.id), 5000);
    this.refrescar();
  }

  cerrarAlerta(id: number): void {
    this.alertas = this.alertas.filter((a) => a.id !== id);
    this.refrescar();
  }

  // ---------- formato ----------

  hh(hora: number): string {
    return String(hora).padStart(2, '0') + ':00';
  }

  rango(b: Bloque): string {
    return this.hh(b.hora) + '–' + this.hh(b.hora + this.parametros.duracionSesion);
  }

  // ---------- parametros y franja ----------

  private armarHoras(): void {
    // la última hora de inicio posible deja espacio para una sesión completa
    const ultima = this.parametros.horaFin - this.parametros.duracionSesion;
    this.horas = [];
    for (let h = this.parametros.horaInicio; h <= ultima; h++) {
      this.horas.push(h);
    }
  }

  private cargarParametros(): void {
    this.http.get<Parametros>(this.apiUrl + '/parametros').subscribe({
      next: (p) => {
        this.parametros = p;
        this.armarHoras();
        this.refrescar();
      },
      error: () => this.alerta('aviso', 'No se pudieron leer los parámetros del servidor, se usan los del caso de estudio')
    });
  }

  // Restricción dura: devuelve el motivo por el que una sesión que empieza en
  // esa hora no se puede programar, o null si es válida.
  motivoBloqueo(dia: string, hora: number): string | null {
    const p = this.parametros;
    const fin = hora + p.duracionSesion;
    const cierre = dia === 'SAB' ? p.sabadoFin : p.horaFin;
    if (fin > cierre) {
      return `Una sesión de ${p.duracionSesion} horas que empieza a las ${this.hh(hora)} termina a las ` +
        `${this.hh(fin)} y el ${dia} se programa hasta las ${this.hh(cierre)}`;
    }
    if (hora < p.almuerzoFin && fin > p.almuerzoInicio) {
      return `No se programan sesiones entre las ${this.hh(p.almuerzoInicio)} y las ${this.hh(p.almuerzoFin)}`;
    }
    return null;
  }

  // Restricción blanda: con 2 o 3 sesiones por semana se prefiere evitar días
  // consecutivos. Devuelve el par de días en conflicto, o null.
  private consecutivos(curso?: string): string | null {
    if (!curso) {
      return null;
    }
    const sesiones = this.bloques.filter((b) => b.curso === curso);
    if (sesiones.length < 2 || sesiones.length > 3) {
      return null;
    }
    const indices = sesiones.map((b) => this.dias.indexOf(b.dia)).sort((a, b) => a - b);
    for (let i = 0; i < indices.length - 1; i++) {
      if (indices[i + 1] - indices[i] === 1) {
        return `${this.dias[indices[i]]} y ${this.dias[indices[i + 1]]}`;
      }
    }
    return null;
  }

  tieneConsecutivos(b: Bloque): boolean {
    return this.consecutivos(b.curso) !== null;
  }

  private revisarConsecutivos(curso?: string): void {
    const par = this.consecutivos(curso);
    if (par) {
      this.alerta('aviso',
        `Preferencia no cumplida: el curso ${curso} tiene sesiones en días consecutivos (${par})`);
    }
  }

  // ---------- carga y creación ----------

  cargarBloques(): void {
    this.http.get<Bloque[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.bloques = data;
        this.refrescar();
      },
      error: () => this.alerta('error', 'No se pudieron cargar los bloques del servidor')
    });
  }

  agregar(codigo: string, curso: string): void {
    codigo = codigo.trim();
    curso = curso.trim();
    if (!codigo || !curso) {
      this.alerta('aviso', 'Escribe el código y el curso del bloque');
      return;
    }
    this.http.post<Bloque>(this.apiUrl, { codigo, curso, dia: 'LUN', hora: 8 }).subscribe({
      next: (creado) => {
        this.bloques.push(creado);
        this.alerta('exito',
          `Sesión ${creado.codigo} del curso ${creado.curso} creada en ${creado.dia} ${this.rango(creado)}`);
        this.revisarConsecutivos(creado.curso);
      },
      error: (err) => {
        const msg = err.error?.message ?? 'No se pudo crear el bloque';
        this.alerta(err.status === 400 || err.status === 409 ? 'aviso' : 'error', msg);
      }
    });
  }

  // ---------- websocket ----------

  private conectar(): void {
    this.ws = new WebSocket(this.wsUrl);
    this.ws.onopen = () => {
      this.conectado = true;
      this.alerta('exito', 'Conectado al servidor');
    };
    this.ws.onclose = () => {
      this.conectado = false;
      if (!this.destruido) {
        this.alerta('error', 'Se perdió la conexión con el servidor');
      }
    };
    this.ws.onmessage = (e) => this.recibir(e.data);
  }

  // formato: TIPO|codigo|dia|hora|version
  private recibir(texto: string): void {
    const p = texto.split('|');

    if (p[0] === 'ERROR') {
      this.alerta('error', 'El servidor rechazó la operación: ' + p[1]);
      this.cargarBloques(); // se vuelve a lo que tiene el servidor
      return;
    }

    const codigo = p[1];
    const dia = p[2];
    const hora = Number(p[3]);
    const version = Number(p[4]);
    this.actualizarLocal({ codigo, dia, hora, version });
    const bloque = this.bloques.find((b) => b.codigo === codigo);
    const destino = `${dia} ${this.hh(hora)}–${this.hh(hora + this.parametros.duracionSesion)}`;

    if (p[0] === 'ACTUALIZADO') {
      const propio = this.pendientes.get(codigo);
      if (propio && propio.dia === dia && propio.hora === hora) {
        this.pendientes.delete(codigo);
        this.alerta('exito', `Sesión ${codigo} movida a ${destino}`);
      } else {
        this.alerta('info', `Otro usuario movió la sesión ${codigo} a ${destino}`);
      }
      this.revisarConsecutivos(bloque?.curso);
    } else if (p[0] === 'CONFLICTO') {
      this.pendientes.delete(codigo);
      this.alerta('aviso',
        `Conflicto de versión: la sesión ${codigo} ya la había movido otra persona. Quedó en ${destino}`);
    }
  }

  private actualizarLocal(nuevo: Bloque): void {
    const i = this.bloques.findIndex((b) => b.codigo === nuevo.codigo);
    if (i === -1) {
      this.bloques.push(nuevo);
    } else {
      this.bloques[i] = { ...this.bloques[i], ...nuevo };
    }
    this.refrescar();
  }

  // ---------- arrastrar y soltar ----------

  bloquesEn(dia: string, hora: number): Bloque[] {
    return this.bloques.filter((b) => b.dia === dia && b.hora === hora);
  }

  iniciarArrastre(bloque: Bloque): void {
    this.bloqueArrastrado = bloque;
  }

  permitirSoltar(event: DragEvent): void {
    event.preventDefault();
  }

  soltar(event: DragEvent, dia: string, hora: number): void {
    event.preventDefault();
    const b = this.bloqueArrastrado;
    this.bloqueArrastrado = null;
    if (!b || (b.dia === dia && b.hora === hora)) {
      return;
    }

    // restricciones duras: no se envía nada si no se cumplen
    const motivo = this.motivoBloqueo(dia, hora);
    if (motivo) {
      this.alerta('error', `No se puede mover ${b.codigo}: ${motivo}`);
      return;
    }
    if (this.ws?.readyState !== WebSocket.OPEN) {
      this.alerta('error', `Sin conexión con el servidor, no se pudo mover ${b.codigo}`);
      return;
    }

    // se mueve en pantalla de una vez, el servidor confirma o lo devuelve
    this.pendientes.set(b.codigo, { dia, hora });
    const versionBase = b.version;
    b.dia = dia;
    b.hora = hora;
    this.ws.send(`MOVER|${b.codigo}|${dia}|${hora}|${versionBase}`);
    this.refrescar();
  }
}
