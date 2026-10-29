package co.edu.autonoma.tracking_envios_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "paquetes")
@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaqueteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ToString.Include
    @EqualsAndHashCode.Include

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_envio", nullable = false, unique = true)
    private EnvioEntity envio;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "peso_kg", nullable = false, precision = 8, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "alto_cm", precision = 8, scale = 2)
    private BigDecimal altoCm;

    @Column(name = "ancho_cm", precision = 8, scale = 2)
    private BigDecimal anchoCm;

    @Column(name = "largo_cm", precision = 8, scale = 2)
    private BigDecimal largoCm;

    @Column(name = "valor_declarado", precision = 12, scale = 2)
    private BigDecimal valorDeclarado;

    @Column(name = "fragil", nullable = false)
    private Boolean fragil;
   
}
