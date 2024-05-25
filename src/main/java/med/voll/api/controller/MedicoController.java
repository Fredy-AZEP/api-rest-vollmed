package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.direccion.DatosDireccion;
import med.voll.api.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    //REGISTRO DE LOS DATOS DE MEDICO
    @PostMapping
    public ResponseEntity<DatosRespuestaMedico> registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico, UriComponentsBuilder uriComponentsBuilder){
        //System.out.println("El request llego correctamente.");
        //System.out.println(datosRegistroMedico);
        Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
        DatosRespuestaMedico datosRespuestaMedico = new DatosRespuestaMedico(medico.getId(),medico.getNombre(),medico.getEmail(),medico.getTelefono(),medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(),medico.getDireccion().getDistrito(),medico.getDireccion().getCiudad(),medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento()));
        URI url = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaMedico);
    }

    //LISTADO DE LOS DATOS DE MEDICO
    @GetMapping
    public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 2) Pageable paginacion){
        //return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));
    }

    /*@GetMapping
    public List<Medico> listadoMedicos(){
        return medicoRepository.findAll();
    }*/

    //ACTUALIZACION DE DATOS MEDICO
    @PutMapping
    @Transactional
    public ResponseEntity actualizaMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico){
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);
        return ResponseEntity.ok(new DatosRespuestaMedico(medico.getId(),medico.getNombre(),medico.getEmail(),medico.getTelefono(),medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(),medico.getDireccion().getDistrito(),medico.getDireccion().getCiudad(),medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento())));
    }

    //DELETE LOGICO
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarMedico(@PathVariable Long id){
        Medico medico = medicoRepository.getReferenceById(id);
        medico.desactivarMedico();
        return ResponseEntity.noContent().build();
    }


    //DELETE A NIVEL DE DB
    /*@DeleteMapping("/{id}")
    @Transactional
    public void eliminarMedico(@PathVariable Long id){
        Medico medico = medicoRepository.getReferenceById(id);
        medicoRepository.delete(medico);
    }*/

    //OBTENER DATOS DE UN MEDICO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id){
        Medico medico = medicoRepository.getReferenceById(id);
        var datosMedico = new DatosRespuestaMedico(medico.getId(),medico.getNombre(),medico.getEmail(),medico.getTelefono(),medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(),medico.getDireccion().getDistrito(),medico.getDireccion().getCiudad(),medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosMedico);
    }
}