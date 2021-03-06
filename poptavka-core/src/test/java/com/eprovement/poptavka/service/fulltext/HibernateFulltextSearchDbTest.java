package com.eprovement.poptavka.service.fulltext;

import com.eprovement.poptavka.base.RealDbTest;
import com.eprovement.poptavka.domain.demand.Demand;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Very similar to the {@link HibernateFulltextSearchIntegrationTest} with exception that it connects to the real db!
 *
 * <p>
 *     In this case that means that these tests highly depend on database state (table Demand is subject to frequent
 *     changes) and can be corrupted.
 *
 * @author Juraj Martinka
 *         Date: 20.5.11
 */
// This test is obsolete because original content of database has been deleted.
// Must be ignored until some new data will are available.
@Ignore
public class HibernateFulltextSearchDbTest extends RealDbTest {

    @Autowired
    private FulltextSearchService fulltextSearchService;

    private static volatile boolean fulltextIndexInitialized = false;

    /**
     * Creates initial fulltext index - only once per test class due the performance reasons.
     */
    @Before
    public synchronized void createInitialFulltextIndex() {
        if (!fulltextIndexInitialized) {
            fulltextSearchService.createInitialFulltextIndex();
            fulltextIndexInitialized = true;
        }
    }


    @Test
    public void testSearchBasic() {
        final List<Demand> foundDemands =
                fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "dohodou");
        Assert.assertTrue(foundDemands.size() > 9);
    }

    @Test
    public void testSearchCaseInsensitive() {
        final List<Demand> foundDemands =
                fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "maďarska");
        Assert.assertTrue(CollectionUtils.isNotEmpty(foundDemands));
    }

    @Test
    public void testSearchAccentInsensitive() {
        final List<Demand> foundDemands =
                fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "mnozstvi");
        Assert.assertTrue(CollectionUtils.isNotEmpty(foundDemands));
        Assert.assertTrue(foundDemands.size() > 10);
    }

    @Test
    public void testSearchStemming() {
        final List<Demand> foundDemands =
                fulltextSearchService.search(Demand.class, Demand.DEMAND_FULLTEXT_FIELDS, "dohoda");
        Assert.assertTrue(foundDemands.size() > 9);
    }
}
