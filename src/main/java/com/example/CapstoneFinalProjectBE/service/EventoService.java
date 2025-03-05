package com.example.CapstoneFinalProjectBE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Evento;
import com.example.CapstoneFinalProjectBE.model.Ospedale;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.repository.EventoRepository;
import com.example.CapstoneFinalProjectBE.repository.OspedaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventoService {

    @Autowired
    private EventoRepository eventoRepo;

    @Autowired
    private OspedaleRepository ospedaleRepo;

    @Autowired
    private Cloudinary cloudinary;

    //  CREAZIONE EVENTO (Solo Admin) con gestione immagine
    @PreAuthorize("hasRole('ADMIN')")
    public String creaEvento(EventoDTO dto, MultipartFile imgEvento) throws IOException {
        Ospedale ospedale = ospedaleRepo.findById(dto.getOspedale().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + dto.getOspedale().getId()));

        Evento evento = dtoToEntity(dto);
        evento.setOspedale(ospedale);

        //  Se l'Admin carica un'immagine, la carichiamo su Cloudinary
        if (imgEvento != null && !imgEvento.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgEvento.getBytes(), ObjectUtils.emptyMap());
            evento.setImgEvento((String) uploadResult.get("secure_url"));
        }

        eventoRepo.save(evento);
        return "Evento creato con ID: " + evento.getId();
    }

    //  OTTIENI UN EVENTO PER ID (Accesso libero)
    public EventoDTO getEventoById(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        return entityToDto(evento);
    }

    //  OTTIENI TUTTI GLI EVENTI (Accesso libero)
    public Page<EventoDTO> getAllEventi(Pageable pageable) {
        Page<Evento> listaEventi = eventoRepo.findAll(pageable);
        List<EventoDTO> listaEventiDTO = new ArrayList<>();

        for (Evento evento : listaEventi.getContent()) {
            listaEventiDTO.add(entityToDto(evento));
        }

        return new PageImpl<>(listaEventiDTO, pageable, listaEventi.getTotalElements());
    }

    //  MODIFICA EVENTO (Solo Admin) - Possibilità di aggiornare immagine
    @PreAuthorize("hasRole('ADMIN')")
    public String modificaEvento(Long id, EventoDTO dto, MultipartFile imgFile) throws IOException {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setData(dto.getData());

        //  Se l'Admin carica una nuova immagine, la aggiorniamo
        if (imgFile != null && !imgFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgFile.getBytes(), ObjectUtils.emptyMap());
            evento.setImgEvento((String) uploadResult.get("secure_url"));
        }

        eventoRepo.save(evento);
        return "Evento con ID: " + id + " modificato con successo.";
    }

    //  ELIMINA EVENTO (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEvento(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        eventoRepo.delete(evento);
        return "Evento con ID: " + id + " eliminato con successo.";
    }

    //  TRAVASO DTO → ENTITY
    private Evento dtoToEntity(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setData(dto.getData());
        evento.setImgEvento(dto.getImgEvento()); // Se l'Admin passa un URL, lo usiamo direttamente
        return evento;
    }

    // TRAVASO ENTITY → DTO (con ospedale senza eventi per evitare ricorsione infinita)
    private EventoDTO entityToDto(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitolo(evento.getTitolo());
        dto.setDescrizione(evento.getDescrizione());
        dto.setData(evento.getData());
        dto.setImgEvento(evento.getImgEvento());

        //  Assegniamo l'ospedale SENZA la lista degli eventi per evitare loop
        if (evento.getOspedale() != null) {
            OspedaleDTO ospedaleDTO = new OspedaleDTO();
            ospedaleDTO.setId(evento.getOspedale().getId());
            ospedaleDTO.setNome(evento.getOspedale().getNome());
            ospedaleDTO.setIndirizzo(evento.getOspedale().getIndirizzo());
            ospedaleDTO.setEmail(evento.getOspedale().getEmail());
            ospedaleDTO.setImgOspedale(evento.getOspedale().getImgOspedale());
            ospedaleDTO.setEventi(null); // Evitiamo di creare loop infinito
            dto.setOspedale(ospedaleDTO);
        }

        return dto;
    }

}
