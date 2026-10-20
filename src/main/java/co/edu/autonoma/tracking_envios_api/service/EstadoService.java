package co.edu.autonoma.tracking_envios_api.service;

import org.springframework.stereotype.Service;

import co.edu.autonoma.tracking_envios_api.dto.EstadoResponse;

@Service 
public class EstadoService {
public EstadoResponse consultar(){
    return new 
    EstadoResponse("tu-api","disponible");
}
}
