package co.edu.autonoma.tracking_envios_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Spring Boot de seguimiento de envíos.
 *
 * <p>La configuración habilita el descubrimiento de componentes y la
 * autoconfiguración de Spring Boot para el paquete de la aplicación.</p>
 */
@SpringBootApplication
public class TrackingEnviosApiApplication {

	/**
	 * Inicia el contexto de Spring Boot.
	 *
	 * @param args argumentos recibidos al ejecutar la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(TrackingEnviosApiApplication.class, args);
	}

}
