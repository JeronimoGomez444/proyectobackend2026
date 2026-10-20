package co.edu.autonoma.tracking_envios_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_novedades")
public class AuditoriaNovedadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_envio", nullable = false)
    private EnvioEntity envio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity usuario;

    @Column(name = "id_novedad_eliminada", nullable = false)
    private int idNovedadEliminada;

    @Column(name = "descripcion_snapshot", nullable = false, length = 255)
    private String descripcionSnapshot;

    @Column(name = "fecha_novedad_snapshot", nullable = false)
    private LocalDateTime fechaNovedadSnapshot;

    @Column(name = "fecha_eliminacion", nullable = false)
    private LocalDateTime fechaEliminacion;

    @Column(name = "motivo", length = 255)
    private String motivo;

    public AuditoriaNovedadEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EnvioEntity getEnvio() {
        return envio;
    }

    public void setEnvio(EnvioEntity envio) {
        this.envio = envio;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public int getIdNovedadEliminada() {
        return idNovedadEliminada;
    }

    public void setIdNovedadEliminada(int idNovedadEliminada) {
        this.idNovedadEliminada = idNovedadEliminada;
    }

    public String getDescripcionSnapshot() {
        return descripcionSnapshot;
    }

    public void setDescripcionSnapshot(String descripcionSnapshot) {
        this.descripcionSnapshot = descripcionSnapshot;
    }

    public LocalDateTime getFechaNovedadSnapshot() {
        return fechaNovedadSnapshot;
    }

    public void setFechaNovedadSnapshot(LocalDateTime fechaNovedadSnapshot) {
        this.fechaNovedadSnapshot = fechaNovedadSnapshot;
    }

    public LocalDateTime getFechaEliminacion() {
        return fechaEliminacion;
    }

    public void setFechaEliminacion(LocalDateTime fechaEliminacion) {
        this.fechaEliminacion = fechaEliminacion;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}