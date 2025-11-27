package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;

import com.example.demo.models.Todo;
import com.example.demo.models.TodoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*") // utile si tu veux tester avec Swing
public class TodoController {

    private final TodoRepository repository;

    public TodoController(TodoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Todo> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Todo add(@RequestBody Todo todo) {
        return repository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @RequestBody Todo todo) {
        Todo existing = repository.findById(id).orElseThrow();
        existing.setTitle(todo.getTitle());
        existing.setCompleted(todo.isCompleted());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}