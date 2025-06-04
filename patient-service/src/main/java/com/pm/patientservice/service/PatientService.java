package com.pm.patientservice.service;


import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exceptions.EmailAlreadyExistsException;
import com.pm.patientservice.exceptions.PatientNotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PatientResponseDTO getPatientById(UUID id) {
        return patientRepository.findById(id)
                .map(PatientMapper::toDTO)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
    }

    public PatientResponseDTO createPatient(PatientRequestDTO request) {
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email "
                    + "already exists " + request.getEmail());
        }
        Patient newPatient = patientRepository.save(
                PatientMapper.toModel(request));

        billingServiceGrpcClient.createBillingAccount(
                newPatient.getId().toString(),
                newPatient.getName(),
                newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toDTO(newPatient);
    }

    @Transactional
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO request) {
        return patientRepository.findById(id)
                .map(patient -> {
//                    boolean emailChanged = !patient.getEmail().equals(request.getEmail());
//                    boolean emailExists = patientRepository.existsByEmail(request.getEmail());
//                    if (emailChanged && emailExists) {
//                        throw new EmailAlreadyExistsException("A patient with this email already exists " + request.getEmail());
//                    }
                    if (patientRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                        throw new EmailAlreadyExistsException("A patient with this email already exists " + request.getEmail());
                    }

                    patient.setName(request.getName());
                    patient.setEmail(request.getEmail());
                    patient.setAddress(request.getAddress());
                    patient.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
                    return patientRepository.save(patient);
                })
                .map(PatientMapper::toDTO)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
    }


    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));
        patientRepository.delete(patient);
    }
}
