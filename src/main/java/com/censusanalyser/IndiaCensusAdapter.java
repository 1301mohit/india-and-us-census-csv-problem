package com.censusanalyser;

import com.opencsvbuilder.CSVBuilderException;
import com.opencsvbuilder.CSVBuilderFactory;
import com.opencsvbuilder.ICSVBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public class IndiaCensusAdapter extends CensusAdapter {

    @Override
    public Map<String, CensusDAO> loadCensusData(CensusAnalyser.Country country, String... csvFilePath) throws CensusAnalyserException{
        try {
            Map<String, CensusDAO> censusStateMap = super.loadCensusData(IndiaCensusCSV.class, csvFilePath[0]);
            this.loadIndiaStateCode( censusStateMap,csvFilePath[1]);
            return censusStateMap;
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new CensusAnalyserException(e.getMessage(), CensusAnalyserException.ExceptionType.SOME_FILE_ISSUE);
        }
    }

    private int loadIndiaStateCode(Map<String, CensusDAO> censusStateMap, String... csvFilePath) throws CensusAnalyserException {
        int counter = 0;
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath[0]));) {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IndiaStateCodeCsv> stateCsvIterator = csvBuilder.getCsvFileIterator(reader, IndiaStateCodeCsv.class);
            Iterable<IndiaStateCodeCsv> csvIterable = () -> stateCsvIterator;
            StreamSupport.stream((csvIterable.spliterator()), false)
                    .filter(csvState -> censusStateMap.get(csvState.state) != null)
                    .forEach(csvState -> censusStateMap.get(csvState.state).stateCode = csvState.stateCode);
            return censusStateMap.size();
        } catch (IOException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CSVBuilderException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.ERROR_FROM_CSV_BUILDER);
        } catch (RuntimeException e) {
            throw new CensusAnalyserException(e.getMessage(),
                    CensusAnalyserException.ExceptionType.SOME_FILE_ISSUE);
        }
    }
}
