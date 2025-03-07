package com.example.CapstoneFinalProjectBE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Evento;
import com.example.CapstoneFinalProjectBE.model.Ospedale;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.repository.EventoRepository;
import com.example.CapstoneFinalProjectBE.repository.OspedaleRepository;
import com.example.CapstoneFinalProjectBE.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private UtenteRepository utenteRepo;

    // CREAZIONE EVENTO (Con immagine)
    public String creaEvento(EventoDTO dto, MultipartFile imgEvento) throws IOException {
        Evento evento = dtoToEntity(dto);

        // Se è presente un'immagine, la carichiamo su Cloudinary
        if (imgEvento != null && !imgEvento.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgEvento.getBytes(), ObjectUtils.emptyMap());
            evento.setImgEvento((String) uploadResult.get("secure_url"));
        }

        eventoRepo.save(evento);
        return "Evento creato con ID: " + evento.getId();
    }

    // PRENOTAZIONE UTENTE A UN EVENTO (Solo per utenti normali, no admin)
    public String prenotaUtente(Long eventoId, Long utenteId) {
        // Controlla se l'evento esiste
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + eventoId));

        // Controlla se l'utente esiste
        Utente utente = utenteRepo.findById(utenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + utenteId));

        // Impedisce la prenotazione agli admin
        if (utente.getIsAdmin()) {
            return "Gli admin non possono prenotarsi agli eventi!";
        }

        // Controlla se l'utente è già prenotato
        if (evento.getUtenti().contains(utente)) {
            return "L'utente è già prenotato a questo evento!";
        }

        // Aggiungi l'utente alla lista prenotati dell'evento
        evento.getUtenti().add(utente);
        eventoRepo.save(evento);

        return "Utente con ID " + utenteId + " prenotato con successo all'evento con ID " + eventoId;
    }

    // OTTENERE UN EVENTO PER ID
    public EventoDTO getEventoById(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));
        return entityToDto(evento);
    }

    // OTTENERE TUTTI GLI EVENTI
    public Page<EventoDTO> getAllEventi(Pageable pageable) {
        Page<Evento> listaEventi = eventoRepo.findAll(pageable);
        List<EventoDTO> listaEventiDTO = new ArrayList<>();

        for (Evento evento : listaEventi.getContent()) {
            listaEventiDTO.add(entityToDto(evento));
        }

        return new PageImpl<>(listaEventiDTO, pageable, listaEventi.getTotalElements());
    }

    // OTTENERE GLI EVENTI A CUI L'UTENTE È PRENOTATO
    public Page<EventoDTO> getEventiPrenotati(Long utenteId, Pageable pageable) {
        // Controlla se l'utente esiste
        Utente utente = utenteRepo.findById(utenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + utenteId));

        // Ottiene la lista degli eventi prenotati dall'utente
        List<Evento> eventiPrenotati = new ArrayList<>(utente.getEventiPrenotati());

        // Converte gli eventi in DTO
        List<EventoDTO> listaEventiDTO = new ArrayList<>();
        for (Evento evento : eventiPrenotati) {
            listaEventiDTO.add(entityToDto(evento));
        }

        return new PageImpl<>(listaEventiDTO, pageable, listaEventiDTO.size());
    }

    // MODIFICA EVENTO (Modifica solo i campi inviati)
    public String modificaEvento(Long id, EventoDTO dto) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        if (dto.getTitolo() != null) evento.setTitolo(dto.getTitolo());
        if (dto.getDescrizione() != null) evento.setDescrizione(dto.getDescrizione());
        if (dto.getData() != null) evento.setData(dto.getData());

        eventoRepo.save(evento);
        return "Evento con ID: " + id + " modificato con successo.";
    }

    // ELIMINA EVENTO
    public String deleteEvento(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        eventoRepo.delete(evento);
        return "Evento con ID: " + id + " eliminato con successo.";
    }

    // TRAVASO DTO → ENTITY
    private Evento dtoToEntity(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setTitolo(dto.getTitolo());
        evento.setDescrizione(dto.getDescrizione());
        evento.setData(dto.getData());
        evento.setImgEvento(dto.getImgEvento());

        // **Associa l'ospedale se presente**
        if (dto.getOspedale() != null && dto.getOspedale().getId() > 0) {
            Ospedale ospedale = new Ospedale();
            ospedale.setId(dto.getOspedale().getId()); // Manteniamo solo l'ID senza caricare l'intera entità
            evento.setOspedale(ospedale);
        }

        return evento;
    }

    // TRAVASO ENTITY → DTO
    private EventoDTO entityToDto(Evento evento) {
        EventoDTO dto = new EventoDTO();
        dto.setId(evento.getId());
        dto.setTitolo(evento.getTitolo());
        dto.setDescrizione(evento.getDescrizione());
        dto.setData(evento.getData());
        dto.setImgEvento(evento.getImgEvento());

        // **Convertiamo l'ospedale associato**
        if (evento.getOspedale() != null) {
            OspedaleDTO ospedaleDTO = new OspedaleDTO();
            ospedaleDTO.setId(evento.getOspedale().getId());
            ospedaleDTO.setNome(evento.getOspedale().getNome());
            ospedaleDTO.setIndirizzo(evento.getOspedale().getIndirizzo());
            ospedaleDTO.setEmail(evento.getOspedale().getEmail());
            ospedaleDTO.setImgOspedale(evento.getOspedale().getImgOspedale());

            dto.setOspedale(ospedaleDTO);
        }

        return dto;
    }
}
