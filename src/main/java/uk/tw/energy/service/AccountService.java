package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountService {

    @Autowired
    private Map<String , String > meterToTariffIds;

    public AccountService(Map<String, String> meterToTariffIds) {
        this.meterToTariffIds = meterToTariffIds;
    }

    public String getTariffForMeter(String meterId) {
        return meterToTariffIds.get(meterId);
    }
}
