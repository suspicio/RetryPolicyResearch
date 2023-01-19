package com.master.RetryPolicy.service;

import com.datastax.oss.driver.internal.core.cql.DefaultRow;
import com.datastax.oss.driver.shaded.guava.common.collect.Iterators;
import com.master.RetryPolicy.entity.DummyTable;
import com.master.RetryPolicy.repository.DummyTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

@EnableAsync
@Service
public class DummyTableService {
    @Autowired
    private DummyTableRepository dummyTableRepository;

    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;

    private Queue <Pair< String, Long >> inputQueue = new LinkedList<>();
    private Set<Pair<String, Long>> processingSet = new HashSet<>();
    private long count = 0;

    @PostConstruct
    @Async
    public void BulkRunning() {
        System.err.println("PROCESS STARTED");
        dummyTableRepository.deleteAll();
        asyncTaskExecutor.execute(this::consumer);
        asyncTaskExecutor.execute(this::producer);
    }

    @Async
    public void consumer() {
        System.err.println("CONSUMER STARTED");
        long startTime = System.nanoTime() / 1000000;
        long inSecondOp = 0;
        while (true) {
            if (this.inputQueue.size() > 0) {
                if (inSecondOp < 10) {
                    inSecondOp++;
                    Pair<String, Long> request = this.inputQueue.poll();
                    this.processingSet.add(request);
                    switch (request.getFirst()) {
                        case "count":       asyncTaskExecutor.execute(() -> BulkCount(request.getSecond())); break;
                        case "read":        asyncTaskExecutor.execute(() -> BulkRead(request.getSecond())); break;
                        case "mult_save":   asyncTaskExecutor.execute(() -> BulkMultipleSave(request.getSecond())); break;
                        case "save":        asyncTaskExecutor.execute(() -> BulkSave(request.getSecond())); break;
                        case "update":      asyncTaskExecutor.execute(() -> BulkUpdate(request.getSecond())); break;
                    }
                }
            }
            if (System.nanoTime() / 1000000 - startTime / 1000000 > 1000) {
                System.err.println("Input Queue: " + this.inputQueue.size() + "\nProcessing Set: " + this.processingSet.size());
                startTime = System.nanoTime();
                inSecondOp = 0;
            }
        }
    }

    @Async
    public void producer() {
        System.err.println("PRODUCER STARTED");
        int max = 5;
        int min = 1;
        int range = max - min + 1;
        long op = 0;

        while (true) {
            if (this.inputQueue.size() > 50)
                continue;
            int rand = (int)(Math.random() * range) + min;
            if (rand > 2 && this.count >= 50000)
                rand = 5;

            switch (rand) {
                case 1: this.inputQueue.add(Pair.of("count", ++op)); break;
                case 2: this.inputQueue.add(Pair.of("save", ++op)); break;
                case 3: this.inputQueue.add(Pair.of("mult_save", ++op)); break;
                case 4: this.inputQueue.add(Pair.of("read", ++op)); break;
                case 5: this.inputQueue.add(Pair.of("update", ++op)); break;
            }
        }
    }

    @Async
    public void BulkMultipleSave(long op_id) {
        List<DummyTable> dummyTables = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            dummyTables.add(new DummyTable(UUID.randomUUID(),"test"+i,"testtt"+i, "testt"+i, LocalDate.now()));
        }
        long startTime = System.nanoTime();
        try {
            dummyTableRepository.saveAll(dummyTables);
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.println("Response time Big Save: " + responseTime + "ms");
        processingSet.remove(Pair.of("mult_save", op_id));
    }

    @Async
    public void BulkSave(long op_id) {
        long startTime = System.nanoTime();
        try {
            dummyTableRepository.save(new DummyTable(UUID.randomUUID(),"test"+Math.random(),"testtt"+Math.random(), "testt"+Math.random(), LocalDate.now()));
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.println("Response time Save: " + responseTime + "ms");
        processingSet.remove(Pair.of("save", op_id));
    }

    @Async
    public void BulkCount(long op_id) {
        long startTime = System.nanoTime();
        try {
            this.count = dummyTableRepository.count();
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.println("Response time COUNT: " + responseTime + "ms\nResponse: " + this.count);
        processingSet.remove(Pair.of("count", op_id));
    }

    @Async
    public void BulkRead(long op_id) {
        long startTime = System.nanoTime();
        Iterable<DummyTable> resp = null;
        try {
            resp = dummyTableRepository.findByEmail("testt" + Integer.toString(new Double(Math.round(Math.random() * 1000)).intValue()));
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.print("Response time Read: " + responseTime + "ms\nResponse: ");
        if (resp != null) {
            //Consumer<DummyTable> printInner = dummy -> System.out.print(dummy.toString());
            //resp.forEach(printInner);
            System.out.println(Iterators.size(resp.iterator()));
        } else {
            System.out.println("EMPTY");
        }
        processingSet.remove(Pair.of("read", op_id));
    }

    @Async
    public void BulkUpdate(long op_id) {
        long startTime = System.nanoTime();

        try {
            Iterable<DummyTable> resp = null;
            String randVal = "testt" + Integer.toString(new Double(Math.round(Math.random() * 1000)).intValue());
            resp = dummyTableRepository.findByEmail(randVal);
            List<DefaultRow> defaultRows = new ArrayList<>();
            if (resp != null) {
                Consumer<DummyTable> collectElements = dummy -> defaultRows.add(dummyTableRepository.updateBirthday( dummy.getId(), LocalDate.of(2000, 2, 15)));
                resp.forEach(collectElements);
            }
            System.out.println(defaultRows.get(0).getColumnDefinitions().get(0));
        } catch (Exception e) {
            System.err.println(e);
        }
        long endTime = System.nanoTime();
        long responseTime = (endTime - startTime) / 1000000;
        System.out.println("Response time Update: " + responseTime + "ms");
        processingSet.remove(Pair.of("update", op_id));
    }
}
