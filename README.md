# BABEAgent
Java 프로그램의 수정 없이, 외부자원에 대한 Java 메소드 호출 성능을 측정할 수 있는 javaagent 프로그램     
> Java 프로그램은 DB, file, network등 다양한 외부 자원에 접근하여 데이터를 처리함     
> 안정적인 운영을 위해서는 외부 자원 접근에 대한 성능 모니터링이 필수적임    
> 예를 들어, DB와 같은 경우 DB lock 등 다양한 이유로 쿼리 수행 속도가 지연되는 현상이 발생할 수 있는데   
> 이에 대한 모니터링을 통해 지속적인 점검을 통해 운영 안정성을 확보할 수 있음    

> 일반적으로 메소드 실행 성능 측정은 메소드의 앞, 뒤에 로깅하여 성능읓 측정할 수 있음    
> 하지만 이미 운영환경에서 실행되고 있는 기존 프로그램을 수정하고 테스트하기에는 많은 시간과 노력이 요구됨    
> 이를 해결하기 위해, 클래스 로딩시 바이트 코드를 변환하는 기술인 JavaAgent와 BCI 기술을 활용하여    
> target 메소드 호출시, 로깅을 수행하도록 클래스를 변환하도록 함     

JavaAgent & BCI(ByteCode Instruments)
> JavaAgent는 JVM이 클래스 로딩시에 바이트 코드를 변환할 수 있도록 클래스 로더에 ClassFileTransformer를 등록할 수 있도록 함     
> BCI는 Java Class 파일의 바이트 코드를 수정할 수 있는 기능을 제공함    
> BCI 라이브러리는 일반적으로 ObjectWeb의 ASM과 Apache Commons Project BCEL(ByteCode Engineering Library)을 많이 사용함    
> 본 프로젝트에서는 ASM 라이브러리를 활용하여 개발    


