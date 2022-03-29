package toit.du.tee.grsc.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

//@Entity
@Table(name = "note")
public class NoteEntity {

   @Id
   @GeneratedValue
   private Long id;

   private String title;
   
   @OneToOne
   private ContentEntity content;
   
   @OneToMany
   private List<ContentEntity> comments;
   
   @ManyToMany
   private Set<NoteEntity> referencedNotes;

   private LocalDateTime created;
   private LocalDateTime modified;
   private LocalDateTime accessed;
   
}
