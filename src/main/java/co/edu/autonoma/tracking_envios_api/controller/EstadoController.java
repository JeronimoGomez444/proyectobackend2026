package co.edu.autonoma.tracking_envios_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tracking_envios_api.dto.EstadoResponse;
import co.edu.autonoma.tracking_envios_api.service.EstadoService;

@RestController 
@RequestMapping("/api/estado")
public class EstadoController {
    private final EstadoService service;

public EstadoController(EstadoService service){
    this.service = service;
}


@GetMapping 

public EstadoResponse consultar(){
    return service.consultar();
}


}
