package com.example.CapstoneFinalProjectBE.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.CapstoneFinalProjectBE.exception.ResourceNotFoundException;
import com.example.CapstoneFinalProjectBE.model.Evento;
import com.example.CapstoneFinalProjectBE.model.Ospedale;
import com.example.CapstoneFinalProjectBE.payload.EventoDTO;
import com.example.CapstoneFinalProjectBE.payload.OspedaleDTO;
import com.example.CapstoneFinalProjectBE.repository.OspedaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
@Transactional
public class OspedaleService {

    @Autowired
    private OspedaleRepository ospedaleRepo;

    @Autowired
    private Cloudinary cloudinary;

    // CREAZIONE OSPEDALE (Con supporto immagine)
    public OspedaleDTO creaOspedale(OspedaleDTO dto, MultipartFile imgOspedale) throws IOException {
        Ospedale ospedale = dtoToEntity(dto);

        // Se è presente un'immagine, la carichiamo su Cloudinary
        if (imgOspedale != null && !imgOspedale.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imgOspedale.getBytes(), ObjectUtils.emptyMap());
            ospedale.setImgOspedale((String) uploadResult.get("secure_url"));
        }

        // Salviamo l'ospedale
        ospedale = ospedaleRepo.save(ospedale);

        // Ritorniamo il DTO con i dati salvati
        return entityToDto(ospedale);
    }

    // CERCA OSPEDALE PER NOME CON LISTA DI EVENTI ALL INTERNO
    public OspedaleDTO findByNome(String nome) {
        Optional<Ospedale> ospedale = ospedaleRepo.findByNome(nome);
        return ospedale.map(this::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con nome: " + nome));
    }



    // OTTIENI UN OSPEDALE PER ID (con lista eventi senza riferimenti)
    public OspedaleDTO getOspedaleById(Long id) {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        return entityToDto(ospedale);
    }

    // CERCA TUTTI GLI OSPEDALI SENZA LA LISTA DI EVENTI ALL INTERNO
    public List<OspedaleDTO> getAllOspedali() {
        List<Ospedale> listaOspedali = ospedaleRepo.findAll();
        List<OspedaleDTO> listaOspedaliDTO = new ArrayList<>();

        for (Ospedale ospedale : listaOspedali) {
            listaOspedaliDTO.add(entityToDtoSenzaEventi(ospedale));  // TRAVASO DIRETTO SENZA EVENTI
        }

        return listaOspedaliDTO;
    }

    // MODIFICA OSPEDALE (Modifica solo i campi inviati nel JSON)
    public OspedaleDTO modificaOspedale(Long id, OspedaleDTO dto) {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        // Modifica solo i campi presenti nel DTO
        if (dto.getNome() != null) ospedale.setNome(dto.getNome());
        if (dto.getIndirizzo() != null) ospedale.setIndirizzo(dto.getIndirizzo());
        if (dto.getEmail() != null) ospedale.setEmail(dto.getEmail());

        // Salviamo le modifiche
        ospedale = ospedaleRepo.save(ospedale);

        // Ritorniamo il DTO con i dati aggiornati
        return entityToDto(ospedale);
    }

    // ELIMINA OSPEDALE
    public Map<String, String> deleteOspedale(Long id) {
        Ospedale ospedale = ospedaleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ospedale non trovato con ID: " + id));

        ospedaleRepo.delete(ospedale);

        // Creiamo un JSON con il messaggio di conferma
        Map<String, String> response = new HashMap<>();
        response.put("message", "Ospedale con ID: " + id + " eliminato con successo.");
        return response;
    }

    // TRAVASO DTO → ENTITY
    private Ospedale dtoToEntity(OspedaleDTO dto) {
        Ospedale ospedale = new Ospedale();
        ospedale.setNome(dto.getNome());
        ospedale.setIndirizzo(dto.getIndirizzo());
        ospedale.setEmail(dto.getEmail());
        ospedale.setImgOspedale(dto.getImgOspedale());
        return ospedale;
    }

    // TRAVASO ENTITY → DTO (con lista di eventi SENZA riferimenti all'ospedale per evitare loop)
    private OspedaleDTO entityToDto(Ospedale ospedale) {
        OspedaleDTO dto = new OspedaleDTO();
        dto.setId(ospedale.getId());
        dto.setNome(ospedale.getNome());
        dto.setIndirizzo(ospedale.getIndirizzo());
        dto.setEmail(ospedale.getEmail());
        dto.setImgOspedale(ospedale.getImgOspedale());

        // Passiamo la lista degli eventi SENZA l'ospedale per evitare ricorsione infinita
        List<EventoDTO> eventiDTO = new ArrayList<>();
        if (ospedale.getEventi() != null) {
            for (Evento evento : ospedale.getEventi()) {
                EventoDTO eventoDTO = new EventoDTO();
                eventoDTO.setId(evento.getId());
                eventoDTO.setTitolo(evento.getTitolo());
                eventoDTO.setDescrizione(evento.getDescrizione());
                eventoDTO.setData(evento.getData());
                eventoDTO.setImgEvento(evento.getImgEvento());
                eventiDTO.add(eventoDTO);
            }
        }
        dto.setEventi(eventiDTO);

        return dto;
    }

    // TRAVASO ENTITY → DTO SENZA EVENTI
    private OspedaleDTO entityToDtoSenzaEventi(Ospedale ospedale) {
        OspedaleDTO dto = new OspedaleDTO();
        dto.setId(ospedale.getId());
        dto.setNome(ospedale.getNome());
        dto.setIndirizzo(ospedale.getIndirizzo());
        dto.setEmail(ospedale.getEmail());
        dto.setImgOspedale(ospedale.getImgOspedale());
        return dto;
    }
}
