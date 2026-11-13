package co.edu.autonoma.tracking_envios_api.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
    
class NovedadEntityTest {

    @Test
    void antesDeGuardar_conFechaNula_asignaFechaActual() {
        NovedadEntity novedad = new NovedadEntity();
        novedad.setDescripcion("El paquete sufrió un retraso");
        assertThat(novedad.getFechaRegistro()).isNull();

        novedad.antesDeGuardar();

        assertThat(novedad.getFechaRegistro()).isNotNull();
        assertThat(novedad.getFechaRegistro())
                .isCloseTo(LocalDateTime.now(), within(2, ChronoUnit.SECONDS));
    }
}