package com.censusanalyser;

import com.opencsv.bean.CsvBindByName;

public class IndiaStateCodeCsv {
    @CsvBindByName(column = "State Name", required = true)
    public String state;

    @CsvBindByName(column = "StateCode", required = true)
    public String stateCode;
}
