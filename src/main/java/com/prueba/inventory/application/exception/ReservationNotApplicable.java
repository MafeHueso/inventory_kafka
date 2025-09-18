package com.prueba.inventory.application.exception;

public class ReservationNotApplicable extends RuntimeException {
    public ReservationNotApplicable(String message) {
        super(message);
    }
}
