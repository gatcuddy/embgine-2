package embgine.core;

import java.util.HashMap;
import java.util.Set;

import embgine.core.loaders.BlockLoader;
import embgine.core.loaders.ObjectLoader;
import embgine.core.loaders.ShapeLoader;
import embgine.core.renderers.FontRenderer;
import embgine.core.renderers.Renderer;
import embgine.graphics.ALManagement;
import embgine.graphics.Camera;
import embgine.graphics.Shape;
import embgine.graphics.Sound;
import embgine.graphics.Window;

public class Index {

	private Window window;
	private Camera camera;
	private ALManagement audio;
	
	private HashMap<String, Scene       >        _sceneMap;
	private HashMap<String, Shape       >        _shapeMap;
	private HashMap<String, Global<?>   >       _globalMap;
	private HashMap<String, Sound       >         soundMap;
	private HashMap<String, Font        >          fontMap;
	private HashMap<String, ObjectLoader>  objectLoaderMap;
	private HashMap<String, BlockLoader >         blockMap;
	private HashMap<String, MapReference>  mapReferenceMap;
	private HashMap<String, Map         >           mapMap;
	
	private float gameWidth;
	private float gameHeight;
	private String gameName;
	private boolean debug;
	
	public Index(float gw, float gh, String n, boolean db, Camera c, Window w, ALManagement a) {
		gameWidth = gw;
		gameHeight = gh;
		gameName = n;
		debug = db;
		camera = c;
		window = w;
		audio = a;
		
		Scene.giveIndex(this);
		ShapeLoader.giveCamera(camera);
		
		_loadScenes();
		_loadShapes();
		_loadGlobals();
	}
	
	public void sceneLoad(Scene sc) {
		loadSounds(sc.getSounds());
		loadFonts(sc.getFonts());
		loadGameObjects(sc.getObjects());
		loadBlocks(sc.getBlocks());
		loadMapReferences(sc.getMapReferences());
		loadMaps(sc.getMaps());
		
		sc.initStartMap();
		
		sc.start();
	}
	
	/*
	#################################################################################
	################################# BASE LOADS ####################################
	#################################################################################
	*/
	
	private void _loadScenes() {
		@SuppressWarnings("unchecked")
		Class<? extends Scene>[] classes = (Class<? extends Scene>[])Utils.getClasses("game/scenes");
		int len = classes.length;
		_sceneMap = new HashMap<String, Scene>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			Class<? extends Scene> cl = classes[i];
			try {
				Scene instance = (Scene)cl.getConstructors()[0].newInstance();
				_sceneMap.put(Utils.getHashName(cl), instance);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	protected void _loadShapes() {
		@SuppressWarnings("unchecked")
		Class<? extends ShapeLoader>[] classes = (Class<? extends ShapeLoader>[])Utils.getClasses("game/shapes");
		int len = classes.length;
		_shapeMap = new HashMap<String, Shape>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			Class<? extends ShapeLoader> cl = classes[i];
			try {
				Shape instance = ((ShapeLoader)cl.getConstructors()[0].newInstance()).create();
				_shapeMap.put(Utils.getHashName(cl), instance);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void _loadGlobals() {
		@SuppressWarnings("unchecked")
		Class<? extends Global<?>>[] classes = (Class<? extends Global<?>>[])Utils.getClasses("game/globals");
		int len = classes.length;
		_globalMap = new HashMap<String, Global<?>>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			Class<? extends Global<?>> cl = classes[i];
			try {
				Global<?> instance = (Global<?>)cl.getConstructors()[0].newInstance();
				_globalMap.put(Utils.getHashName(cl), instance);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/*
	#################################################################################
	################################# PER-SCENE LOADS ###############################
	#################################################################################
	*/
	
	private void loadSounds(String[] soundPaths) {
		int len = soundPaths.length;
		soundMap = new HashMap<String, Sound>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			String name = soundPaths[i];
			
			Sound sound = new Sound(name);
			
			StringBuilder build = new StringBuilder(32);
			int nameLen = name.length();
			
			int j = nameLen - 2;
			
			for( ; j > -1; --j) {
				if(name.charAt(j) == '/') {
					break;
				}
			}
			
			for(++j; j < nameLen; ++j) {
				build.append(name.charAt(j));
			}
			
			soundMap.put(build.toString(), sound);
		}
	}
	
	private void loadFonts(Class<? extends Font>[] fonts) {
		int len = fonts.length;
		fontMap = new HashMap<String, Font>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			
			Class<? extends Font> fc = fonts[i];
			
			Font f = null;
			
			try {
				f = (Font)fc.getConstructors()[0].newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			fontMap.put(Utils.getHashName(fc), f);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadGameObjects(Class<? extends ObjectLoader>[] objects) {

		int len = objects.length;
		objectLoaderMap = new HashMap<String, ObjectLoader>(len, 1.0f);
		
		for(int i = 0; i < len; ++i) {
			
			Class<? extends ObjectLoader> oc = objects[i];
					
			ObjectLoader loader = null;
			try {
				loader = (ObjectLoader)oc.getConstructors()[0].newInstance();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			Object[][] templateList = loader.getTemplates();
			int numRenderers = templateList.length;
			Renderer[] rList = new Renderer[numRenderers];
			
			for(int j = 0; j < numRenderers; ++j) {
				Object[] template = templateList[j];
				
				//replace the shape string in the renderer template with the shape instance in here
				template[1] = _getShape((String)template[1]);

				//replace the font string in the renderer template with the font instance in here
				//if it actually has a fontRenderer
				if((Class<? extends Renderer>)template[0].getClass() == FontRenderer.class) {
					template[2] = getFont((String)template[2]);
				}
				
				try {
					Object[] omega = {template};
					
					rList[j] = ((Class<? extends Renderer>)template[0]).getDeclaredConstructor(Object[].class).newInstance(omega);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(-1);
				}
			}

			loader.giveRenderers(rList);
			
			loader.giveType(i);
			
			objectLoaderMap.put(Utils.getHashName(oc), loader);
		}
	}
	
	protected void loadBlocks(Class<? extends BlockLoader>[] blocks) {
		int len = blocks.length;
		blockMap = new HashMap<String, BlockLoader>(len, 1.0f);
		
		for(Class<? extends BlockLoader> cl : blocks) {
			
			try {
				BlockLoader b = (BlockLoader)cl.getConstructors()[0].newInstance();
				blockMap.put(Utils.getHashName(cl), b);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void loadMapReferences(Class<? extends MapReference>[] references) {
		int len = references.length;
		mapReferenceMap = new HashMap<String, MapReference>(len, 1.0f);
		for(int i = 0; i < len; ++i) {
			Class<? extends MapReference> cl = references[i];
			try {
				MapReference instance = ((MapReference)cl.getConstructors()[0].newInstance());
				instance.init(this);
				mapReferenceMap.put(Utils.getHashName(cl), instance);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private void loadMaps(Class<? extends Map>[] maps) {
		int len = maps.length;
		mapMap = new HashMap<String, Map>(len, 1.0f);
		for(Class<? extends Map> cl : maps) {
			try {
				Map m = (Map)cl.getConstructors()[0].newInstance();
				m.init(this);
				mapMap.put(Utils.getHashName(cl), m);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/*
	#################################################################################
	################################# BASE GETTERS ##################################
	#################################################################################
	*/
	
	public String getName() {
		return gameName;
	}

	public Camera getCamera() {
		return camera;
	}
	
	public Window getWindow() {
		return window;
	}
	
	public ALManagement getAudio() {
		return audio;
	}
	
	public float getGameWidth() {
		return gameWidth;
	}
	
	public float getGameHeight() {
		return gameHeight;
	}
	
	public boolean getDebug() {
		return debug;
	}
	
	/*
	#################################################################################
	########################### USER TYPE GETTERS ###################################
	#################################################################################
	*/
	
	public Scene _getScene(String str) {
		/*
		Set<String> p = _sceneMap.keySet();
		for(String s : p) {
			System.out.println(s);
		}
		System.exit(0);
		*/
		return _sceneMap.get(str);
	}
	
	public Shape _getShape(String str) {
		return _shapeMap.get(str);
	}
	
	public Global<?> _getGlobal(String str) {
		return _globalMap.get(str);
	}
	
	public Sound getSound(String str) {
		return soundMap.get(str);
	}
	
	public Font getFont(String str) {
		return fontMap.get(str);
	}
	
	public GameObject getObject(Scene sc, String str) {
		return objectLoaderMap.get(str).create(sc);
	}
	
	public ObjectLoader getObjectLoader(String str) {
		return objectLoaderMap.get(str);
	}
	
	public Block getBlock(String str) {
		return blockMap.get(str).create();
	}
	
	public BlockLoader getBlockLoader(String str) {
		return blockMap.get(str);
	}
	
	public MapReference getMapReference(String str) {
		return mapReferenceMap.get(str);
	}
	
	public Map getMap(String str) {
		return mapMap.get(str);
	}
}