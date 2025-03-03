package dev.hireben.demo.web.bff.presentation.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestAttributeKey {

  // ---------------------------------------------------------------------------//
  // Fields
  // ---------------------------------------------------------------------------//

  public final String TRACE_ID = "TRX-ID";

  public final String ERR_RESP_CODE = "ERR-RESP-CODE";
  public final String ERR_RESP_MSG = "ERR-RESP-MSG";
  public final String ERR_RESP_DATA = "ERR-RESP-DATA";
  public final String ERR_SEVERITY = "ERR-SEV";

}
