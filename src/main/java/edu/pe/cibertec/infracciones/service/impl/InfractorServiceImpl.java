package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.InfractorRequestDTO;
import edu.pe.cibertec.infracciones.dto.InfractorResponseDTO;
import edu.pe.cibertec.infracciones.exception.InfractorNotFoundException;
import edu.pe.cibertec.infracciones.exception.VehiculoNotFoundException;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.IInfractorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InfractorServiceImpl implements IInfractorService {

    private final InfractorRepository infractorRepository;
    private final VehiculoRepository vehiculoRepository;

// pregunta 1
    private final MultaRepository multaRepository;



    @Override
    public InfractorResponseDTO registrarInfractor(InfractorRequestDTO dto) {
        Infractor infractor = new Infractor();
        infractor.setDni(dto.getDni());
        infractor.setNombre(dto.getNombre());
        infractor.setApellido(dto.getApellido());
        infractor.setEmail(dto.getEmail());
        infractor.setBloqueado(false);
        return mapToResponse(infractorRepository.save(infractor));
    }

    @Override
    public InfractorResponseDTO obtenerInfractorPorId(Long id) {
        Infractor infractor = infractorRepository.findById(id)
                .orElseThrow(() -> new InfractorNotFoundException(id));
        return mapToResponse(infractor);
    }

    @Override
    public List<InfractorResponseDTO> obtenerTodos() {
        return infractorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void asignarVehiculo(Long infractorId, Long vehiculoId) {
        Infractor infractor = infractorRepository.findById(infractorId)
                .orElseThrow(() -> new InfractorNotFoundException(infractorId));
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        infractor.getVehiculos().add(vehiculo);
        infractorRepository.save(infractor);
    }


    private InfractorResponseDTO mapToResponse(Infractor infractor) {
        InfractorResponseDTO dto = new InfractorResponseDTO();
        dto.setId(infractor.getId());
        dto.setDni(infractor.getDni());
        dto.setNombre(infractor.getNombre());
        dto.setApellido(infractor.getApellido());
        dto.setEmail(infractor.getEmail());
        dto.setBloqueado(infractor.isBloqueado());
        return dto;
    }


    @Override
    public Double calcularDeuda(Long id) {
        List<Multa> multas = multaRepository.findByInfractorId(id);
        double deudaTotal = 0.0;

        for (Multa multa : multas) {
            Double montoMulta = multa.getMonto();

            if ("PENDIENTE".equals(multa.getEstado().name())) {
                deudaTotal += montoMulta;
            }
            else if ("VENCIDA".equals(multa.getEstado().name())) {
                deudaTotal += montoMulta * 1.15;
            }
        }
        return deudaTotal;
    }

    @Override
    @Transactional
    public void desasignarVehiculo(Long infractorId, Long vehiculoId) {
        Infractor infractor = infractorRepository.findById(infractorId)
                .orElseThrow(() -> new RuntimeException("Infractor no encontrado"));

        List<Multa> multasDelVehiculo = multaRepository.findByVehiculo_Id(vehiculoId);

        boolean tienePendientes = multasDelVehiculo.stream()
                .anyMatch(m -> m.getEstado().name().equals("PENDIENTE"));

        if (tienePendientes) {
            throw new RuntimeException("No se puede desasignar: El vehículo tiene multas PENDIENTES.");
        }

        infractor.getVehiculos().removeIf(v -> v.getId().equals(vehiculoId));
        infractorRepository.save(infractor);
    }
}