package com.hhplus.hhpluspaymentservice.interfaces.controller.payment;

import com.hhplus.hhpluspaymentservice.domain.payment.PaymentService;
import com.hhplus.hhpluspaymentservice.interfaces.common.dto.ApiResultResponse;
import com.hhplus.hhpluspaymentservice.interfaces.controller.payment.dto.PaymentDto;
import com.hhplus.hhpluspaymentservice.support.aop.TraceLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "결제", description = "Payment-controller")
@TraceLog
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 요청
     *
     * @param request reservationId, userId, token, amount 정보
     * @return ApiResultResponse 결제 결과를 반환한다.
     */
    @Operation(summary = "결제 요청")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PaymentDto.Response.class)))
    @PostMapping("/pay")
    public ApiResultResponse<PaymentDto.Response> pay(@RequestBody @Valid PaymentDto.Request request) {

        return ApiResultResponse.ok(PaymentDto.Response.of(paymentService.pay(request.toCreateCommand())));
    }

    /**
     * 결제 상태 확인
     *
     * @param paymentId paymentId 정보
     * @return ApiResultResponse 결제 결과를 반환한다.
     */
    @Operation(summary = "결제 상태 확인")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PaymentDto.Response.class)))
    @GetMapping("/pay/{paymentId}/status")
    public ApiResultResponse<PaymentDto.Response> getPaymentStatus(@PathVariable(value = "paymentId") @NotNull Long paymentId) {

        return ApiResultResponse.ok(PaymentDto.Response.of(paymentService.getPayment(paymentId)));
    }
}