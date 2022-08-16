import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Esta tarefa usa o framework Java fork-join e o paralelo
 * streams framework para calcular o tamanho em bytes de um determinado arquivo,
 * bem como todos os arquivos de um determinado diretório.
 */
public class FileCounterParallelStream
       extends AbstractFileCounter {

    FileCounterParallelStream(File file) {
        super(file);
    }

    private FileCounterParallelStream(File file,
                                      AtomicLong documentCount,
                                      AtomicLong folderCount) {
        super(file, documentCount, folderCount);
    }

    /**
     * Este método retorna o tamanho em bytes de um arquivo, bem
     * como todos os arquivos em pastas acessíveis a partir deste de um diretório.
     */
    @Override
    protected Long compute() {
        return Stream
            // Converter a lista de ficheiros num fluxo de ficheiros.
            .of(Objects.requireNonNull(mFile.listFiles()))

            // Converter o fluxo sequencial para um fluxo paralelo.
            .parallel()

            // Processar cada ficheiro de acordo com o seu tipo.
            .mapToLong(file -> {
                // Determinar se mFile é um ficheiro (documento) vs. a
                // directório (pasta).
                    if (file.isFile()) {
                        // Incrementar a contagem dos documentos.
                        mDocumentCount.incrementAndGet();

                        // Devolver o tamanho do ficheiro.
                        return mFile.length();
                    } else {
                        // Aumentar a contagem das pastas.
                        mFolderCount.incrementAndGet();

                        // Conta o número de ficheiros a partir de uma função recursiva em
                        // uma determinada pasta.
                        return new FileCounterParallelStream(file,
                                                             mDocumentCount,
                                                             mFolderCount).compute();

                    }
                })

            // Soma o tamanho de todos os ficheiro
            .sum();
    }
}

