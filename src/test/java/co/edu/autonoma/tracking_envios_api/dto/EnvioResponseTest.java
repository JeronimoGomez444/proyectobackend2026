package co.edu.autonoma.tracking_envios_api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

class EnvioResponseTest {

    @Test
    void desde_envioSinRepartidor_idRepartidorEsNulo() {
        // Arrange
        UsuarioEntity operador = new UsuarioEntity();
        operador.setId(1);
        EnvioEntity envio = new EnvioEntity();
        envio.setId(7);
        envio.setCodigoSeguimiento("ENV-ABC12345");
        envio.setEstadoActual(EstadoEnvio.CREADO);
        envio.setOperador(operador);

        // Act
        EnvioResponse respuesta = EnvioResponse.desde(envio);

        // Assert
        assertThat(respuesta.id()).isEqualTo(7);
        assertThat(respuesta.codigoSeguimiento()).isEqualTo("ENV-ABC12345");
        assertThat(respuesta.idOperador()).isEqualTo(1);
        assertThat(respuesta.idRepartidor()).isNull();
    }
}