repackage com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.difficult.DifficultRequest;
import com.example.Othellodifficult.dto.difficult.DifficultResponse;
import com.example.Othellodifficult.service.DifficultService;
import com.example.Othellodifficult.token.TokenHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/difficult")
@AllArgsConstructor
@CrossOrigin
public class DifficultController {

    private final DifficultService difficultService;

    @GetMapping
    @Operation(summary = "Lấy danh sách độ khó")
    public List<DifficultResponse> getList(@RequestHeader("Authorization") String token){
        return difficultService.getList(TokenHandler.getUserIdFromToken(token));
    }

    @PutMapping
    @Operation(summary = "Chỉnh sửa độ khó")
    public List<DifficultResponse> exchange(@RequestBody List<DifficultRequest> requestList,
                                            @RequestHeader("Authorization") String token){
        Long id;
        return difficultService.exchange(requestList, TokenHandler.getUserIdFromToken(token));
    }

    @PutMapping("/reset")
    @Operation(summary = "Reset độ khó")
    public List<DifficultResponse> reset(@RequestHeader("Authorization") String token){
        return difficultService.reset(TokenHandler.getUserIdFromToken(token));
    }
}
