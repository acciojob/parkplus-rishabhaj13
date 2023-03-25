package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        try {
            Reservation reservation = new Reservation();
            User user = null;
            ParkingLot parkingLot = null;
            user = userRepository3.findById(userId).get();
            // set reservation with user
            reservation.setUser(user);
            //  reservation.setPayment(null);
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();

            List<Spot> spotList = parkingLot.getSpotList();


            if (user == null && parkingLot == null) {
                reservation.setSpot(null);

                reservationRepository3.save(reservation);

                throw new Exception("Cannot make reservation");
            }
            int minimumPrice = Integer.MAX_VALUE;
            Spot spotReq = null;
            for (Spot spot : spotList) {
                if (spot.getOccupied() == false) {
                    if (typeOfSpot(spot.getSpotType()) >= numberOfWheels && spot.getPricePerHour() * timeInHours < minimumPrice) {
                        minimumPrice = spot.getPricePerHour() * timeInHours;
                        spotReq = spot;

                    }
                }

            }
            if (spotReq == null) {
                reservation.setSpot(null);
                reservationRepository3.save(reservation);
                throw new Exception("Cannot make reservation");
            }

            // set attributes of reservation
            reservation.setNumberOfHours(timeInHours);
            reservation.setSpot(spotReq);

            // set user attributes
            List<Reservation> reservationList = user.getReservationList();
            if (reservationList == null) {
                reservationList = new ArrayList<>();
            }
            reservationList.add(reservation);
            user.setReservationList(reservationList);
            // set spot attributes
            List<Reservation> reservations = spotReq.getReservationList();
            if (reservations == null) {
                reservations = new ArrayList<>();
            }
            reservations.add(reservation);
            spotReq.setReservationList(reservations);
            spotReq.setOccupied(true);
            userRepository3.save(user);
            spotRepository3.save(spotReq);
            return reservation;

        } catch (Exception e) {
            return  null;
        }
    }
    public int typeOfSpot(SpotType spotType)
    {
        if(spotType.equals(SpotType.TWO_WHEELER))
            return 2;
        else if(spotType.equals(SpotType.FOUR_WHEELER))
            return 4;
        return Integer.MAX_VALUE;
    }
}
