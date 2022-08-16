import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class FileCounterWalkStream
        extends AbstractFileCounter {

    FileCounterWalkStream(File rootFile) {
        super(rootFile);
    }

    @Override
    protected Long compute() {
        try {
            return Files

                    .walk(mFile.toPath())

                    .mapToLong(entry -> {
                        if (Files.isDirectory(entry)) {

                            mFolderCount.incrementAndGet();

                            return 0;
                        } else {
                            mDocumentCount.incrementAndGet();

                            return entry.toFile().length();
                        }
                    })

                    .sum();
        } catch (IOException e) {

            return 0L;

        }
    }
}
