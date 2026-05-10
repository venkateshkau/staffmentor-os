package com.staffmentor.coach.controller;

import com.staffmentor.coach.dto.KnowledgeSnippetDto;
import com.staffmentor.coach.entity.KnowledgeSnippet;
import com.staffmentor.coach.repository.KnowledgeSnippetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/knowledge-snippets")
@RequiredArgsConstructor
public class KnowledgeSnippetController {

    private final KnowledgeSnippetRepository repository;

    @GetMapping("/due")
    public List<KnowledgeSnippetDto> getDueSnippets() {
        return repository.findByNextReviewDateLessThanEqual(LocalDate.now())
                .stream()
                .map(s -> new KnowledgeSnippetDto(s.getId(), s.getContent(), s.getNextReviewDate(), s.getIntervalDays()))
                .toList();
    }

    public record ReviewRequest(boolean remembered) {}

    @PostMapping("/{id}/review")
    public void reviewSnippet(@PathVariable UUID id, @RequestBody ReviewRequest request) {
        KnowledgeSnippet snippet = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found"));
        
        if (request.remembered()) {
            snippet.setIntervalDays(snippet.getIntervalDays() * 2); // Exponential backoff
        } else {
            snippet.setIntervalDays(1); // Reset to 1 day if forgotten
        }
        
        snippet.setNextReviewDate(LocalDate.now().plusDays(snippet.getIntervalDays()));
        repository.save(snippet);
    }
}
