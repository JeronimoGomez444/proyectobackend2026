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
import java.math.BigDecimal;

@Entity
@Table(name = "paquetes")
public class PaqueteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    public PaqueteEntity() {
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getAltoCm() {
        return altoCm;
    }

    public void setAltoCm(BigDecimal altoCm) {
        this.altoCm = altoCm;
    }

    public BigDecimal getAnchoCm() {
        return anchoCm;
    }

    public void setAnchoCm(BigDecimal anchoCm) {
        this.anchoCm = anchoCm;
    }

    public BigDecimal getLargoCm() {
        return largoCm;
    }

    public void setLargoCm(BigDecimal largoCm) {
        this.largoCm = largoCm;
    }

    public BigDecimal getValorDeclarado() {
        return valorDeclarado;
    }

    public void setValorDeclarado(BigDecimal valorDeclarado) {
        this.valorDeclarado = valorDeclarado;
    }

    public Boolean getFragil() {
        return fragil;
    }

    public void setFragil(Boolean fragil) {
        this.fragil = fragil;
    }
}