package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.IInfractorService;
import edu.pe.cibertec.infracciones.service.IMultaService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class InfraccionesServiceTest {

    @Autowired
    private IInfractorService infractorService;
    @Autowired
    private InfractorRepository infractorRepository;

    @Autowired
    private IMultaService multaService;

    @Autowired
    private MultaRepository multaRepository;

    @Test
    void testCalcularDeudaConRecargo() {

        Long infractorId = 1L;
        Double resultadoObtenido = infractorService.calcularDeuda(infractorId);
        Double resultadoEsperado = 545.0;
        assertEquals(resultadoEsperado, resultadoObtenido, "El cálculo de la deuda con " +
                "recargo del 15% es incorrecto");
    }


    @Test
    @Transactional
    void testDesasignarVehiculoExitoso() {

        Long infractorId = 1L;
        Long vehiculoId = 1L;

        infractorService.desasignarVehiculo(infractorId, vehiculoId);

        var infractorActualizado = infractorRepository.findById(infractorId).get();
        boolean aunTieneElVehiculo = infractorActualizado.getVehiculos().stream()
                .anyMatch(v -> v.getId().equals(vehiculoId));
        assertFalse(aunTieneElVehiculo, "El vehículo no fue removido de la lista del infractor");
    }



    @Test
    @Transactional
    void testTransferirMultaExitoso() {

        Long multaId = 1L;
        Long nuevoInfractorId = 2L;

        multaService.transferirMulta(multaId, nuevoInfractorId);
        var multaActualizada = multaRepository.findById(multaId).get();

        assertEquals(nuevoInfractorId, multaActualizada.getInfractor().getId(),
                "La multa no fue transferida al nuevo infractor correctamente");
    }
}