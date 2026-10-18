package pl.piatek.catering.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.piatek.catering.dto.menu.MenuItemAdminResponse;
import pl.piatek.catering.dto.menu.MenuItemRequest;
import pl.piatek.catering.service.MenuItemService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/menu-items")
@RequiredArgsConstructor
public class AdminMenuItemController {

    private final MenuItemService service;

    @GetMapping
    public List<MenuItemAdminResponse> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public MenuItemAdminResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemAdminResponse create(@Valid @RequestBody MenuItemRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public MenuItemAdminResponse update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}