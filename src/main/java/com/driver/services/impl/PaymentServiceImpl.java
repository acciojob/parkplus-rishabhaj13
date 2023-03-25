package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Payment payment = new Payment();
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        int price = reservation.getSpot().getPricePerHour();
        int bill = reservation.getNumberOfHours()*price;
        if(amountSent < bill)
        {
            throw new Exception("Insufficient Amount");
        }
        if(mode.equalsIgnoreCase("CASH") || mode.equalsIgnoreCase("CARD")|| mode.equalsIgnoreCase("UPI"))
        {
            if(mode.equalsIgnoreCase("CASH"))
            {
                payment.setPaymentMode(PaymentMode.CASH);
            }
            else if (mode.equalsIgnoreCase("CARD"))
            {
                payment.setPaymentMode(PaymentMode.CARD);
            } else if (mode.equalsIgnoreCase("UPI")) {
                payment.setPaymentMode(PaymentMode.UPI);

            }
            payment.setPaymentCompleted(true);
            payment.setReservation(reservation);
            reservation.setPayment(payment);
            reservationRepository2.save(reservation);
            return payment;
        }
        else
        {
            throw new Exception("Payment mode not detected");
        }
    }
}
