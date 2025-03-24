package com.example.CapstoneFinalProjectBE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Evento;
import com.example.CapstoneFinalProjectBE.model.Ospedale;
import com.example.CapstoneFinalProjectBE.model.Utente;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.payload.UtenteDTO;
import com.example.CapstoneFinalProjectBE.payload.response.UtenteSenzaEventiDTO;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private EmailService emailService;

    // CREAZIONE EVENTO (Con immagine)
    public EventoDTO creaEvento(EventoDTO dto, MultipartFile imgEvento) throws IOException {
        Evento evento = dtoToEntity(dto);

        // Se è presente un'immagine, la carichiamo su Cloudinary
        if (imgEvento != null && !imgEvento.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgEvento.getBytes(), ObjectUtils.emptyMap());
            evento.setImgEvento((String) uploadResult.get("secure_url"));
        }

        evento = eventoRepo.save(evento);
        return entityToDto(evento);
    }

    // PRENOTAZIONE UTENTE A UN EVENTO (Solo per utenti normali, no admin)
    public Map<String, Object> prenotaUtente(Long eventoId, Long utenteId) {
        Map<String, Object> response = new HashMap<>();

        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + eventoId));

        Utente utente = utenteRepo.findById(utenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + utenteId));

        if (utente.getIsAdmin()) {
            response.put("message", "Gli admin non possono prenotarsi agli eventi!");
            return response;
        }

        if (evento.getUtenti().contains(utente)) {
            response.put("message", "L'utente è già prenotato a questo evento!");
            return response;
        }

        evento.getUtenti().add(utente);
        eventoRepo.save(evento);

        //  Invio email di conferma
        try {
            String corpo = "Ciao " + utente.getNome() + ",\n\n"
                    + "hai prenotato con successo l'evento \"" + evento.getTitolo() + "\" del " + evento.getData() + ".\n"
                    + "Luogo: " + evento.getOspedale().getNome() + ", " + evento.getOspedale().getIndirizzo() + "\n\n"
                    + "Grazie per la tua partecipazione!\n"
                    + "Lo staff di Give Joy ❤️";

            emailService.inviaEmail(utente.getEmail(), "Conferma prenotazione evento", corpo);
        } catch (Exception e) {
            response.put("message", "Prenotazione effettuata, ma errore nell'invio dell'email: " + e.getMessage());
            response.put("evento", entityToDto(evento));
            return response;
        }

        response.put("message", "Utente prenotato con successo!");
        response.put("evento", entityToDto(evento));
        return response;
    }

    // OTTIENI LA LISTA DI UTENTI PRENOTATI A UN EVENTO
    public List<UtenteSenzaEventiDTO> getUtentiPrenotati(Long idEvento) {
        Evento evento = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + idEvento));

        List<UtenteSenzaEventiDTO> utentiPrenotatiDTO = new ArrayList<>();

        for (Utente utente : evento.getUtenti()) {
            utentiPrenotatiDTO.add(utenteService.entityToDtoSenzaEventi(utente));
        }

        return utentiPrenotatiDTO;
    }

    // ANNULLARE UNA PRENOTAZIONE
    public Map<String, Object> cancellaPrenotazione(Long eventoId, Long utenteId) {
        Map<String, Object> response = new HashMap<>();

        // Controlla se l'evento esiste
        Evento evento = eventoRepo.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + eventoId));

        // Controlla se l'utente esiste
        Utente utente = utenteRepo.findById(utenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con ID: " + utenteId));

        // Controlla se l'utente è già prenotato all'evento
        if (!evento.getUtenti().contains(utente)) {
            response.put("message", "L'utente con ID " + utenteId + " non è prenotato a questo evento.");
            return response;
        }

        // Rimuove l'utente dalla lista dei prenotati
        evento.getUtenti().remove(utente);
        eventoRepo.save(evento);

        // Prepara la risposta JSON
        response.put("message", "Prenotazione annullata con successo per l'utente con ID " + utenteId + " dall'evento con ID " + eventoId);
        response.put("evento", entityToDto(evento));
        return response;
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
    public List<EventoDTO> getEventiPrenotati(Long utenteId) {
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

        return listaEventiDTO;
    }


    // CERCA EVENTO PER TITOLO DATA O ENTRAMBI
    public List<EventoDTO> findByTitoloOrData(String titolo, LocalDate data) {
        List<Evento> eventi;

        if (titolo != null && data != null) {
            eventi = eventoRepo.findByTitoloContainingAndData(titolo, data);
        } else if (titolo != null) {
            eventi = eventoRepo.findByTitoloContaining(titolo);
        } else if (data != null) {
            eventi = eventoRepo.findByData(data);
        } else {
            throw new IllegalArgumentException("Devi specificare almeno un parametro di ricerca (titolo o data).");
        }

        // Se la lista è vuota, restituiamo un errore personalizzato
        if (eventi.isEmpty()) {
            throw new ResourceNotFoundException("Nessun evento trovato con Titolo: '" + titolo + "' e Data: '" + data + "'");
        }

        return eventi.stream().map(this::entityToDto).toList();
    }

    // MODIFICA EVENTO (Modifica solo i campi inviati)
    public EventoDTO modificaEvento(Long id, EventoDTO dto) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        if (dto.getTitolo() != null) evento.setTitolo(dto.getTitolo());
        if (dto.getDescrizione() != null) evento.setDescrizione(dto.getDescrizione());
        if (dto.getData() != null) evento.setData(dto.getData());
        if (dto.getOspedale() != null && dto.getOspedale().getId() > 0) {
            Ospedale ospedale = new Ospedale();
            ospedale.setId(dto.getOspedale().getId());
            evento.setOspedale(ospedale);
        }

        evento = eventoRepo.save(evento);
        return entityToDto(evento);
    }

    // ELIMINA EVENTO
    public Map<String, String> deleteEvento(Long id) {
        Evento evento = eventoRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento non trovato con ID: " + id));

        eventoRepo.delete(evento);

        return Map.of("message", "Evento con ID: " + id + " eliminato con successo.");
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
