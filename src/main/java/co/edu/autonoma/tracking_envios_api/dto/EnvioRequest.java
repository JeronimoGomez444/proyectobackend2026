package co.edu.autonoma.tracking_envios_api.dto;

public record EnvioRequest(Integer idOperador, PaqueteRequest paquete, DestinatarioRequest destinatario) { }