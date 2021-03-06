package com.nilsson.vinylrecordsales.image;

import com.nilsson.vinylrecordsales.image.upload.ImageUploadFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    private static final String EXAMPLE_FILE_URL = "http://url-to-example-file.com";
    private static final String ANOTHER_EXAMPLE_FILE_URL = "http://url-to-another-example-file.com";
    private ImageServiceImpl imageUploadService;

    @Mock
    ImageUploadFacade imageUploadFacade;
    @Mock
    private UrlRepository urlRepository;
    @Mock
    private File anExampleFile;
    @Mock
    private File anotherExampleFile;


    @BeforeEach
    void setUp() {
        imageUploadService = new ImageServiceImpl(imageUploadFacade, urlRepository);
    }

    @Test
    void shouldUploadImages() throws MalformedURLException {
        //given
        URL exampleFileUrl = new URL(EXAMPLE_FILE_URL);
        when(imageUploadFacade.uploadImage(anExampleFile)).thenReturn(Mono.just(exampleFileUrl));
        URL anotherExampleFileUrl = new URL(ANOTHER_EXAMPLE_FILE_URL);
        when(imageUploadFacade.uploadImage(anotherExampleFile)).thenReturn(Mono.just(anotherExampleFileUrl));
        //when
        Flux<URL> urls = imageUploadService.uploadImages(List.of(anExampleFile, anotherExampleFile));
        //then
        StepVerifier.create(urls)
                .expectNext(exampleFileUrl, anotherExampleFileUrl)
                .verifyComplete();
        verify(imageUploadFacade).uploadImage(anExampleFile);
        verify(imageUploadFacade).uploadImage(anotherExampleFile);
        verifyNoMoreInteractions(imageUploadFacade);
    }

    @Test
    void shouldStoreUrls() throws MalformedURLException {
        //given
        URL exampleFileUrl = new URL(EXAMPLE_FILE_URL);

        //when
        imageUploadService.storeURLs(Flux.just(exampleFileUrl));

        //then
        verify(urlRepository).add(exampleFileUrl);
        verifyNoMoreInteractions(urlRepository);
        verifyNoInteractions(imageUploadFacade);
    }

    @Test
    void shouldNotStoreAnythingIfZeroUrlsToStore() {
        //when
        imageUploadService.storeURLs(Flux.empty());
        //then
        verifyNoInteractions(urlRepository);
        verifyNoInteractions(imageUploadFacade);
    }

    @Test
    void shouldGetUrlByInsertionOrder() throws MalformedURLException {
        //given
        URL exampleFileUrl = new URL(EXAMPLE_FILE_URL);
        when(urlRepository.poll()).thenReturn(exampleFileUrl);

        //when
        Mono<URL> url = imageUploadService.pollUrl();

        //then
        StepVerifier.create(url)
                .expectNext(exampleFileUrl)
                .verifyComplete();

        verify(urlRepository).poll();
        verifyNoInteractions(imageUploadFacade);
    }

    @Test
    void haveStoredURLs() {
        //given
        when(urlRepository.haveStoredURLs()).thenReturn(true);
        //when
        assertThat(imageUploadService.haveStoredURLs()).isTrue();
        //then
        verify(urlRepository).haveStoredURLs();
        verifyNoMoreInteractions(urlRepository);
        verifyNoInteractions(imageUploadFacade);
    }

    @Test
    void haventStoredURLs() {
        //given
        when(urlRepository.haveStoredURLs()).thenReturn(false);
        //when
        assertThat(imageUploadService.haveStoredURLs()).isFalse();
        //then
        verify(urlRepository).haveStoredURLs();
        verifyNoMoreInteractions(urlRepository);
        verifyNoInteractions(imageUploadFacade);
    }
}