package com.pm.patientservice.exceptions;

import java.util.UUID;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
