package com.snbnk.onboarding.domain;

import java.util.Set;

public class RequiredDocuments {

    public static final Set<String> REQUIRED = Set.of(
            "PASSPORT",
            "DRIVER_LICENSE",
            "PROOF_OF_ADDRESS",
            "BANK_STATEMENT",
            "TAX_ID"
    );
}
