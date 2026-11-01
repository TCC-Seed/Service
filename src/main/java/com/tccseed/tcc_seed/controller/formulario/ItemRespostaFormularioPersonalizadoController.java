package com.tccseed.tcc_seed.controller.formulario;

import com.tccseed.tcc_seed.controller.formulario.dto.ItemRespostaFormularioPersonalizadoRequest;
import com.tccseed.tcc_seed.domain.entity.ItemRespostaFormularioPersonalizado;
import com.tccseed.tcc_seed.domain.entity.id.ItemRespostaFormularioPersonalizadoId;
import com.tccseed.tcc_seed.service.formulario.ItemRespostaFormularioPersonalizadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-respostas-formularios-personalizados")
@RequiredArgsConstructor
public class ItemRespostaFormularioPersonalizadoController {
    private final ItemRespostaFormularioPersonalizadoService service;

    @PostMapping
    public ResponseEntity<ItemRespostaFormularioPersonalizado> criar(@RequestBody ItemRespostaFormularioPersonalizadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request.toEntity()));
    }

    @GetMapping
    public List<ItemRespostaFormularioPersonalizado> listar() {
        return service.listar();
    }

    @GetMapping("/{idResposta}/{idPergunta}")
    public ItemRespostaFormularioPersonalizado buscarPorId(@PathVariable Long idResposta, @PathVariable Long idPergunta) {
        return service.buscarPorId(id(idResposta, idPergunta));
    }

    @PutMapping("/{idResposta}/{idPergunta}")
    public ItemRespostaFormularioPersonalizado atualizar(@PathVariable Long idResposta, @PathVariable Long idPergunta, @RequestBody ItemRespostaFormularioPersonalizadoRequest request) {
        return service.atualizar(id(idResposta, idPergunta), request.toEntity());
    }

    @DeleteMapping("/{idResposta}/{idPergunta}")
    public ResponseEntity<Void> excluir(@PathVariable Long idResposta, @PathVariable Long idPergunta) {
        service.excluir(id(idResposta, idPergunta));
        return ResponseEntity.noContent().build();
    }

    private ItemRespostaFormularioPersonalizadoId id(Long idResposta, Long idPergunta) {
        ItemRespostaFormularioPersonalizadoId id = new ItemRespostaFormularioPersonalizadoId();
        id.setIdResposta(idResposta);
        id.setIdPergunta(idPergunta);
        return id;
    }

}
