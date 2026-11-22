package com.agrotec.web;

import com.agrotec.dto.SyncDto;
import com.agrotec.security.AuthUser;
import com.agrotec.service.SyncService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@Tag(name = "Sincronización", description = "RF-10: subir cambios hechos sin conexión y bajar solo lo nuevo")
public class SyncController {

    private final SyncService sync;

    public SyncController(SyncService sync) {
        this.sync = sync;
    }

    @PostMapping
    public SyncDto.Response sincronizar(@AuthenticationPrincipal AuthUser user,
                                        @Valid @RequestBody SyncDto.Request r) {
        return sync.sincronizar(user.id(), r);
    }

    @GetMapping("/cambios")
    public SyncDto.Cambios cambios(@AuthenticationPrincipal AuthUser user,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde) {
        return sync.cambios(user.id(), desde);
    }
}
