package org.boncey.cdripper;

import fr.tokazio.ripper.RippingStatus;

public interface CDRippingProgressListener {

    void onProgress(RippingStatus status);
}
