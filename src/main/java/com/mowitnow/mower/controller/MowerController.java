package com.mowitnow.mower.controller;

import com.mowitnow.mower.dto.BatchMowerRequest;
import com.mowitnow.mower.dto.MowerCommandRequest;
import com.mowitnow.mower.dto.MowerResponse;
import com.mowitnow.mower.engine.Mower;
import com.mowitnow.mower.engine.Remote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mower")
public class MowerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MowerController.class);

    @Autowired
    private Remote remote;

    @PostMapping("/execute")
    public ResponseEntity<MowerResponse> executeSingleMower(@RequestBody MowerCommandRequest request) {
        try {
            LOGGER.info("Executing single mower command: {}", request.getCommands());
            
            String[] lawnDims = request.getLawnDimensions().split(" ");
            int lawnX = Integer.parseInt(lawnDims[0]);
            int lawnY = Integer.parseInt(lawnDims[1]);
            remote.prepareMowing(lawnX, lawnY);

            String[] mowerPos = request.getMowerPosition().split(" ");
            int x = Integer.parseInt(mowerPos[0]);
            int y = Integer.parseInt(mowerPos[1]);
            char direction = mowerPos[2].charAt(0);

            Mower mower = remote.startMower(x, y, direction);
            mower.execute(request.getCommands());

            MowerResponse response = new MowerResponse(
                mower.toString().split("name=")[1].split(",")[0],
                mower.getX(),
                mower.getY(),
                mower.getPointer()
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error executing mower command", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MowerResponse>> executeBatchMowers(@RequestBody BatchMowerRequest request) {
        try {
            LOGGER.info("Executing batch mower commands for {} mowers", request.getMowers().size());
            
            String[] lawnDims = request.getLawnDimensions().split(" ");
            int lawnX = Integer.parseInt(lawnDims[0]);
            int lawnY = Integer.parseInt(lawnDims[1]);
            remote.prepareMowing(lawnX, lawnY);

            List<MowerResponse> responses = new ArrayList<>();
            
            for (MowerCommandRequest mowerRequest : request.getMowers()) {
                String[] mowerPos = mowerRequest.getMowerPosition().split(" ");
                int x = Integer.parseInt(mowerPos[0]);
                int y = Integer.parseInt(mowerPos[1]);
                char direction = mowerPos[2].charAt(0);

                Mower mower = remote.startMower(x, y, direction);
                mower.execute(mowerRequest.getCommands());

                MowerResponse response = new MowerResponse(
                    mower.toString().split("name=")[1].split(",")[0],
                    mower.getX(),
                    mower.getY(),
                    mower.getPointer()
                );
                responses.add(response);
            }

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            LOGGER.error("Error executing batch mower commands", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/health")
    public String health() {
        return "Mower service is running";
    }
}