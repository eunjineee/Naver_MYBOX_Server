package back.server.mybox.Domain.repository;

import back.server.mybox.Domain.entity.FileEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFilename(String filename);
}
