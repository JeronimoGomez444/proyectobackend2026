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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_novedades")
@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditoriaNovedadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
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

   
}