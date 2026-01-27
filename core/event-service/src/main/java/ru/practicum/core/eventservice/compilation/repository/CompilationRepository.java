package ru.practicum.core.eventservice.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.core.eventservice.compilation.model.Compilation;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findByPinned(boolean pinned, Pageable page);
}
