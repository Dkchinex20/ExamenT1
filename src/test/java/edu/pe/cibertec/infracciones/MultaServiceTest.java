package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.*;
import edu.pe.cibertec.infracciones.repository.*;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Esto activa Mockito
class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Captor
    private ArgumentCaptor<Multa> multaCaptor;


    @Test
    void testPregunta4_TransferirMulta_InfractorBloqueado() {
        // GIVEN: Preparamos los datos (Id 1L y Infractor bloqueado)
        Long multaId = 1L;
        Long infractorIdB = 2L;

        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setEstado(EstadoMulta.PENDIENTE);

        Infractor infractorB = new Infractor();
        infractorB.setId(infractorIdB);
        infractorB.setBloqueado(true); // <--- Requisito: Bloqueado = true

        // Configuramos los simulacros (Mocks)
        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(infractorIdB)).thenReturn(Optional.of(infractorB));

        // WHEN & THEN: Ejecutamos y verificamos que salta la excepción
        assertThrows(InfractorBloqueadoException.class, () -> {
            multaService.transferirMulta(multaId, infractorIdB);
        });

        // VERIFY: Verificamos que el repositorio NUNCA llamó a save()
        // porque la excepción detuvo el proceso.
        verify(multaRepository, never()).save(any(Multa.class));
    }
}