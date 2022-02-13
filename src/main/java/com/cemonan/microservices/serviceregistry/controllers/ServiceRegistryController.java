package com.cemonan.microservices.serviceregistry.controllers;

import com.cemonan.microservices.serviceregistry.exception.ServiceNotFoundException;
import com.cemonan.microservices.serviceregistry.lib.ServiceRegistry;
import com.cemonan.microservices.serviceregistry.pojo.Service;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(path = "/services")
public class ServiceRegistryController {
    @Autowired
    ServiceRegistry serviceRegistry;

    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    @PostMapping(path = "/{name}/{version}/{port}")
    public ResponseEntity<Object> addServiceToServiceRegistry(
            @PathVariable String name,
            @PathVariable String version,
            @PathVariable String port
    ) {
        String ip = this.getParsedRemoteAddr(request.getRemoteAddr());
        Long serviceId = serviceRegistry.add(name, version, ip, port);
        Map<String, String> responseBody = new HashMap<String, String>();
        responseBody.put("message", "Service has been registered successfully.");
        responseBody.put("serviceId", serviceId.toString());
        return new ResponseEntity(responseBody, HttpStatus.CREATED);
    }

    private String getParsedRemoteAddr(String remoteAddr) {
        if (remoteAddr.contains("::")) {
            return remoteAddr = "[" + remoteAddr + "]";
        }
        return remoteAddr;
    }

    @GetMapping(path = "/{name}/{version}")
    public ResponseEntity<Object> getServiceFromServiceRegistry(@PathVariable String name, @PathVariable String version) {
        Service service = serviceRegistry.get(name, version);
        if (service == null) {
            throw new ServiceNotFoundException(
                    String.format(
                            "Service with name: %s, version: %s could not be found.",
                            name, version
                    )
            );
        }
        return new ResponseEntity(service, HttpStatus.OK);
    }

    @DeleteMapping(path = "/{name}/{version}/{port}")
    public ResponseEntity<Object> deleteServiceFromServiceRegistry(
            @PathVariable String name,
            @PathVariable String version,
            @PathVariable String port
    ) {
        String ip = this.getParsedRemoteAddr(request.getRemoteAddr());
        Long deletedServiceId = serviceRegistry.delete(name, version, ip, port);
        if (deletedServiceId == null) {
            throw new ServiceNotFoundException(
                    String.format(
                            "Service with name: %s, version: %s, ip: %s, port: %s could not be found.",
                            name, version, ip, port
                    )
            );
        }
        Map<String, String> responseBody = new HashMap<String, String>();
        responseBody.put("message", "Service has been deleted successfully.");
        responseBody.put("serviceId", deletedServiceId.toString());
        return new ResponseEntity(responseBody, HttpStatus.OK);
    }
}
