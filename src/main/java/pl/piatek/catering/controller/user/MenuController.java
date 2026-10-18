package pl.piatek.catering.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.menu.MenuItemPublicResponse;
import pl.piatek.catering.service.MenuItemService;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuItemService service;

    @GetMapping
    public List<MenuItemPublicResponse> menu() {
        return service.findActive();
    }
}