package uk.tw.energy.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final Map<String, String> smartMeterToPricePlanAccounts;

	public String getPricePlanIdForSmartMeterId(String smartMeterId) {
		return smartMeterToPricePlanAccounts.get(smartMeterId);
	}
}
