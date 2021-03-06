
package com.algaworks.socialbooks.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Optional;

import com.algaworks.socialbooks.domain.Livro;
import com.algaworks.socialbooks.domain.Comentario;
import com.algaworks.socialbooks.services.LivrosService;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.http.CacheControl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.text.normalizer.ICUBinary;



@RestController
@RequestMapping("/livros")
public class LivrosResources {
	
    @Autowired
    private LivrosService livrosService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> listar() {
         return ResponseEntity.status(HttpStatus.OK).body(livrosService.listar());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> salvar(@RequestBody Livro livro) {
        
            livro = livrosService.salvar(livro);
            
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(livro.getId()).toUri();
            
            return ResponseEntity.created(uri).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> buscar(@PathVariable("id") Long id) {
        
        CacheControl cacheControl = CacheControl.maxAge(20, TimeUnit.SECONDS);
        Livro livro = livrosService.buscar(id);
        
        return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(livro);
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
        
        livrosService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> atualizar(@RequestBody Livro livro, @PathVariable("id") Long id) {
        
        livro.setId(id);
        livrosService.atualizar(livro);
        
        return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(value = "/{id}/comentarios",method = RequestMethod.POST)
    public ResponseEntity<?> adicionarComentario(@PathVariable("id") Long livroId, @RequestBody Comentario comentario) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        comentario.setUsuario(auth.getName());
        
        livrosService.salvarComentario(livroId, comentario);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        
        return ResponseEntity.created(uri).build();
    }
    @RequestMapping(value = "/{id}/comentarios",method = RequestMethod.GET)
    public ResponseEntity<List<Comentario>> listarComentarios(@PathVariable("id") Long id) {
         
        List<Comentario> comentarios = livrosService.listaeComentario(id);
        
        return ResponseEntity.status(HttpStatus.OK).body(comentarios);
    }
}