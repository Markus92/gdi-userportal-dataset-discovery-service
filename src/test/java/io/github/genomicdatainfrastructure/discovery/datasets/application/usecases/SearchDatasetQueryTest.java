package io.github.genomicdatainfrastructure.discovery.datasets.application.usecases;

import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetIdsCollector;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.DatasetsRepository;
import io.github.genomicdatainfrastructure.discovery.datasets.application.ports.FacetsBuilder;
import io.github.genomicdatainfrastructure.discovery.model.DatasetSearchQuery;
import io.github.genomicdatainfrastructure.discovery.model.DatasetsSearchResponse;
import io.github.genomicdatainfrastructure.discovery.model.FacetGroup;
import io.github.genomicdatainfrastructure.discovery.model.SearchedDataset;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class SearchDatasetQueryTest {

    @Mock
    private DatasetsRepository repository;

    @Mock
    private Instance<DatasetIdsCollector> collectors;

    @Mock
    private Instance<FacetsBuilder> facetsBuilders;

    @InjectMocks
    private SearchDatasetsQuery searchDatasetQuery;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking the DatasetIdsCollector stream
        DatasetIdsCollector collector1 = Mockito.mock(DatasetIdsCollector.class);
        DatasetIdsCollector collector2 = Mockito.mock(DatasetIdsCollector.class);

        // Mocking what the map function should return
        when(collector1.collect(Mockito.any(), Mockito.any())).thenReturn(List.of("123", "456",
                "789"));
        when(collector2.collect(Mockito.any(), Mockito.any())).thenReturn(List.of("111", "789",
                "1032", "456"));

        when(collectors.stream())
                .thenReturn(Stream.of(collector1, collector2));

        // Mocking the repository search method
        when(repository.search(
                Mockito.anyList(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.anyString()))
                .thenReturn(List.of(
                        SearchedDataset.builder().id("456").build(),
                        SearchedDataset.builder().id("789").build()
                ));

//        // Mocking the FacetsBuilder stream
//        FacetsBuilder builder1 = Mockito.mock(FacetsBuilder.class);
//        FacetsBuilder builder2 = Mockito.mock(FacetsBuilder.class);
//
//        when(facetsBuilders.stream())
//                .thenReturn(Stream.of(builder1, builder2));
//
//        // Mocking what the map function should return
//        when(builder1.build(Mockito.any(), Mockito.any())).thenReturn(FacetGroup.builder().key("beacon").label("beacon").build());
//        when(builder2.build(Mockito.any(), Mockito.any())).thenReturn(FacetGroup.builder().key("ckan").label("dcat-ap").build());

    }

    @Test
    public void testSearchDatasetQuery() {
        DatasetSearchQuery query = new DatasetSearchQuery();
        String accessToken = "testAccessToken";

        DatasetsSearchResponse response = searchDatasetQuery.execute(query, accessToken);

        System.out.println(response);
        assertNotNull(response);
    }
}