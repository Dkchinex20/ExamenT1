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

@ExtendWith(MockitoExtension.class)
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

        Long multaId = 1L;
        Long infractorIdB = 2L;

        Multa multa = new Multa();
        multa.setId(multaId);
        multa.setEstado(EstadoMulta.PENDIENTE);

        Infractor infractorB = new Infractor();
        infractorB.setId(infractorIdB);
        infractorB.setBloqueado(true);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(infractorIdB)).thenReturn(Optional.of(infractorB));

        assertThrows(InfractorBloqueadoException.class, () -> {
            multaService.transferirMulta(multaId, infractorIdB);
        });

        verify(multaRepository, never()).save(any(Multa.class));
    }
}