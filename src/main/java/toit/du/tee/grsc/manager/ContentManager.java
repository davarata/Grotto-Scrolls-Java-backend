package toit.du.tee.grsc.manager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import toit.du.tee.grsc.jpa.ContentEntity;
import toit.du.tee.grsc.jpa.ContentRepository;
import toit.du.tee.grsc.markup.ContentParser;
import toit.du.tee.grsc.rest.Content;
import toit.du.tee.grsc.rest.Node;

@Component
public class ContentManager {

	@Autowired
	private ContentRepository contentRepository;
	@Autowired
	private EntityManager entityManager;
	
	public Content createContent(Content content) {
		return ContentConverter.toRecord(contentRepository.save(ContentConverter.toEntity(content)));
	}
	
	public Content readContent(Long id) {
	   ContentEntity ce = contentRepository.findById(id).get();
	   
      Session session = entityManager.unwrap(Session.class);
      System.out.println("ce: " + session.contains(ce));

      ContentEntity ce2 = new ContentEntity((long) 2, "Contents of 2.");
      System.out.println("ce2: " + session.contains(ce2));
      
      ce2 = session.byId(ContentEntity.class).load((long) 2);
      System.out.println("ce2: " + session.contains(ce2));
      
		return ContentConverter.toRecord(contentRepository.findById(id).get());
	}
	
	public void updateContent(Content content) {
		ContentEntity contentEntity = contentRepository.findById(content.id()).get();
		ContentConverter.updateEntity(contentEntity, content);
		contentEntity.setModified(LocalDateTime.now());
		contentRepository.save(ContentConverter.toEntity(content));
	}
	
	public void deleteContent(long id) {
		contentRepository.deleteById(id);
	}
	
	public List<Content> getList() {
		return contentRepository.getAllContent().stream().
			map(c -> new Content(c.getId(), c.getTitle(), c.getContent(), c.getCreated(), c.getModified(), c.getAccessed())).
			collect(Collectors.toList());
	}
	
	public List<Content> searchByTitle(String searchStr) {
	   return contentRepository.searchByTitle(searchStr).stream().
         map(c -> new Content(c.getId(), c.getTitle(), c.getContent(), c.getCreated(), c.getModified(), c.getAccessed())).
         collect(Collectors.toList());
	}
	
   public List<Node> getFormattedContent(Long id) {
      Content content = ContentConverter.toRecord(contentRepository.findById(id).get());
      
      ContentParser contentFormatter = new ContentParser(content);
      
      return contentFormatter.parse();
   }
   
   public List<Node> getContentNavigation(Long id) {
      return new NavigationCreator().get(getFormattedContent(id));
   }
	
}
