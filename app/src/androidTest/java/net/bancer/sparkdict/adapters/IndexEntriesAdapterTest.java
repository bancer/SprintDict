package net.bancer.sparkdict.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.bancer.sparkdict.domain.core.Book;
import net.bancer.sparkdict.domain.core.DictionaryFiles;
import net.bancer.sparkdict.domain.core.Shelf;
import net.bancer.sparkdict.mocks.Mocks;
import net.bancer.sparkdict.storage.SafDictionaryFilesFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Instrumented test to verify the ordering of suggestions in IndexEntriesAdapter.
 */
@RunWith(AndroidJUnit4.class)
public class IndexEntriesAdapterTest {

    private Context context;

    private Shelf shelf;

    private IndexEntriesAdapter adapter;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        String[] enabledDicts = {
            Mocks.BSE_DICT_NAME,
            Mocks.CAMBRIDGE_DICT_NAME,
            Mocks.MUELLER_DICT_NAME,
            Mocks.WORDNET_DICT_NAME
        };
        DictionaryFiles dictionaryFiles = SafDictionaryFilesFactory.create(context);
        shelf = new Shelf(enabledDicts, dictionaryFiles);
    }

    @After
    public void tearDown() {
        if (adapter != null) {
            adapter.shutdown();
        }
        if (shelf != null) {
            for (Book book : shelf.getBooks()) {
                book.close();
            }
        }
    }

    @Test
    public void searchingBrazilSuggestsEntriesInExpectedOrder() throws InterruptedException {
        Vector<String> entries = new Vector<>();
        adapter = new IndexEntriesAdapter(context, entries) {
            @Override
            protected Shelf getShelf() {
                return shelf;
            }
        };
        // Simulate the user typing "brazil" into the search field.
        adapter.onTextChanged("brazil", 0, 0, "brazil".length());
        Thread.sleep(2000);
        List<String> actual = new ArrayList<>(entries);
        String[] expected = {
            "Brazil",
            "brazil",
            "Brazil nut",
            "brazil nut",
            "Brazil nut tree",
            "brazil-nut",
            "brazil-wood",
            "Brazilian",
            "Brazilian guava",
            "brazilian ironwood"
        };
        assertTrue(
            String.format(
                "Expected at least %d suggestions for \"brazil\" but got %d: %s",
                expected.length,
                actual.size(),
                actual
            ),
            actual.size() >= expected.length
        );
        assertEquals(
            String.format(
                "Unexpected order of the first %d suggestions for \"brazil\"",
                expected.length
            ),
            Arrays.asList(expected),
            actual.subList(0, expected.length)
        );
    }
}
